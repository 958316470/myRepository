package com.nutch.fetcher;

import com.nutch.crawl.CrawlStatus;
import com.nutch.parse.ParseUtil;
import com.nutch.parse.ParserJob;
import com.nutch.host.HostDb;
import com.nutch.net.URLFilterException;
import com.nutch.net.URLFilters;
import com.nutch.net.URLNormalizers;
import com.nutch.protocol.*;
import com.nutch.storage.Host;
import com.nutch.storage.Mark;
import com.nutch.storage.ProtocolStatus;
import com.nutch.storage.WebPage;
import com.nutch.util.TableUtil;
import com.nutch.util.URLUtil;
import crawlercommons.robots.BaseRobotRules;
import org.apache.avro.util.Utf8;
import org.apache.gora.mapreduce.GoraReducer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class FetcherReducer extends GoraReducer<IntWritable, FetchEntry, String, WebPage> {

    public static final Logger LOG = FetcherJob.LOG;
    private final AtomicInteger activeThreads = new AtomicInteger(0);
    private final AtomicInteger spinWaiting = new AtomicInteger(0);
    private final long start = System.currentTimeMillis();
    private final AtomicLong lastRequestStart = new AtomicLong(start);
    private final AtomicLong bytes = new AtomicLong(0);
    private final AtomicInteger pages = new AtomicInteger(0);
    private final AtomicInteger errors = new AtomicInteger(0);
    private QueueFeeder feeder;
    private final List<FetcherThread> fetcherThreads = new ArrayList<FetcherThread>();
    private FetchItemQueues fetchQueues;
    private boolean storingContent;
    private boolean parse;
    private ParseUtil parseUtil;
    private boolean skipTruncated;

    private static class FetchItem {
        WebPage page;
        String queueID;
        String url;
        URL u;

        public FetchItem(String url, WebPage page, URL u, String queueID) {
            this.page = page;
            this.url = url;
            this.u = u;
            this.queueID = queueID;
        }

        public static FetchItem create(String url, WebPage page, String queueMode) {
            String queueID;
            URL u = null;
            try {
                u = new URL(url);
            } catch (final Exception e) {
                LOG.warn("Cannot parse url:" + url, e);
                return null;
            }
            final String proto = u.getProtocol().toLowerCase();
            String host;
            if (FetchItemQueues.QUEUE_MODE_IP.equalsIgnoreCase(queueMode)) {
                try {
                    final InetAddress addr = InetAddress.getByName(u.getHost());
                    host = addr.getHostAddress();
                } catch (final UnknownHostException e) {
                    LOG.warn("Unable to resolve: " + u.getHost() + ", skipping.");
                    return null;
                }
            } else if (FetchItemQueues.QUEUE_MODE_DOMAIN.equalsIgnoreCase(queueMode)) {
                host = URLUtil.getDomainName(u);
                if (host == null) {
                    LOG.warn("Unknown domain for url: " + url + ", using URL string as key");
                    host = u.toExternalForm();
                }
            } else {
                host = u.getHost();
                if (host == null) {
                    LOG.warn("Unknown host for url: " + url + ", using URL string as key");
                    host = u.toExternalForm();
                }
            }
            queueID = proto + "://" + host.toLowerCase();
            return new FetchItem(url, page, u, queueID);
        }

        @Override
        public String toString() {
            return "FetchItem{" +
                    "page=" + page +
                    ", queueID='" + queueID + '\'' +
                    ", url='" + url + '\'' +
                    ", u=" + u +
                    '}';
        }
    }

    private static class FetchItemQueue {
        List<FetchItem> queue = Collections.synchronizedList(new LinkedList<FetchItem>());
        Set<FetchItem> inProgress = Collections.synchronizedSet(new HashSet<FetchItem>());
        AtomicLong nextFetchTime = new AtomicLong();
        long crawlDelay;
        long minCrawlDelay;
        int maxThreads;

        public FetchItemQueue(Configuration conf, int maxThreads, long crawlDelay, long minCrawlDelay) {
            this.maxThreads = maxThreads;
            this.crawlDelay = crawlDelay;
            this.minCrawlDelay = minCrawlDelay;
            setEndTime(System.currentTimeMillis() - crawlDelay);
        }

        public int getQueueSize() {
            return queue.size();
        }

        public int getInProgressSize() {
            return inProgress.size();
        }

        public void finishFetchItem(FetchItem it, boolean asap) {
            if (it != null) {
                inProgress.remove(it);
                setEndTime(System.currentTimeMillis(), asap);
            }
        }

        public void addFetchItem(FetchItem it) {
            if (it == null) {
                return;
            }
            queue.add(it);
        }

        public void addInProgressFetchItem(FetchItem it) {
            if (it == null) {
                return;
            }
            inProgress.add(it);
        }

        public FetchItem getFetchItem() {
            if (inProgress.size() >= maxThreads) {
                return null;
            }
            final long now = System.currentTimeMillis();
            if (nextFetchTime.get() > now) {
                return null;
            }
            FetchItem it = null;
            if (queue.size() == 0) {
                return null;
            }
            try {
                it = queue.remove(0);
                inProgress.add(it);
            } catch (final Exception e) {
                LOG.error("Cannot remove FetchItem from queue or cannot add it to inProgress queue", e);
            }
            return it;
        }

        public synchronized void dump() {
            LOG.info("  maxThreads    = " + maxThreads);
            LOG.info("  inProgress    = " + inProgress.size());
            LOG.info("  crawlDelay    = " + crawlDelay);
            LOG.info("  nextFetchTime = " + nextFetchTime.get());
            LOG.info("  now           = " + System.currentTimeMillis());
            for (int i = 0; i < queue.size(); i++) {
                final FetchItem item = queue.get(i);
                LOG.info("  " + ". " + item.url);
            }
        }

        private void setEndTime(long endTime) {
            setEndTime(endTime, false);
        }

        private void setEndTime(long endTime, boolean asap) {
            if (!asap) {
                nextFetchTime.set(endTime + (maxThreads > 1 ? minCrawlDelay : crawlDelay));
            } else {
                nextFetchTime.set(endTime);
            }
        }

        public synchronized int emptyQueue() {
            int presize = queue.size();
            queue.clear();
            return presize;
        }
    }

    private static class FetchItemQueues {
        public static final String DEFAULT_ID = "default";
        Map<String, FetchItemQueue> queues = new HashMap<String, FetchItemQueue>();
        AtomicInteger totalSize = new AtomicInteger(0);
        int maxThreads;
        String queueMode;
        long crawlDelay;
        long minCrawlDelay;
        Configuration conf;
        long timelimit = -1;
        boolean useHostSettings = false;
        HostDb hostDb = null;

        public static final String QUEUE_MODE_HOST = "byHost";
        public static final String QUEUE_MODE_DOMAIN = "byDomain";
        public static final String QUEUE_MODE_IP = "byIP";

        public FetchItemQueues(Configuration conf) throws IOException {
            this.conf = conf;
            this.maxThreads = conf.getInt("fetcher.threads.per.queue", 1);
            queueMode = conf.get("fetcher.queue.mode", QUEUE_MODE_HOST);
            if (!queueMode.equals(QUEUE_MODE_IP) && !queueMode.equals(QUEUE_MODE_DOMAIN) && !queueMode.equals(QUEUE_MODE_HOST)) {
                LOG.error("Unknown partition mode : " + queueMode + " - forcing to byHost");
                queueMode = QUEUE_MODE_HOST;
            }
            LOG.info("Using queue mode : " + queueMode);
            if (queueMode.equals(QUEUE_MODE_HOST)) {
                useHostSettings = conf.getBoolean("fetcher.queue.use.host.settings", false);
                if (useHostSettings) {
                    LOG.info("Host specific queue settings enabled.");
                    hostDb = new HostDb(conf);
                }
            }
            this.crawlDelay = (long) (conf.getFloat("fetcher.server.delay", 1.0f) * 1000);
            this.minCrawlDelay = (long) (conf.getFloat("fetcher.server.min.delay", 1.0f) * 1000);
            this.timelimit = conf.getLong("fetcher.timelimit", -1);
        }

        public int getTotalSize() {
            return totalSize.get();
        }

        public int getQueueCount() {
            return queues.size();
        }

        public void addFetchItem(String url, WebPage page) {
            final FetchItem it = FetchItem.create(url, page, queueMode);
            if (it != null) {
                addFetchItem(it);
            }
        }

        public synchronized void addFetchItem(FetchItem it) {
            final FetchItemQueue fiq = getFetchItemQueue(it.queueID);
            fiq.addFetchItem(it);
            totalSize.incrementAndGet();
        }

        public void finishFetchItem(FetchItem it) {
            finishFetchItem(it, false);
        }

        public void finishFetchItem(FetchItem it, boolean asap) {
            final FetchItemQueue fiq = queues.get(it.queueID);
            if (fiq == null) {
                LOG.warn("Attempting to finish item from unknown queue: " + it);
                return;
            }
            fiq.finishFetchItem(it, asap);
        }

        public synchronized FetchItemQueue getFetchItemQueue(String id) {
            FetchItemQueue fiq = queues.get(id);
            if (fiq == null) {
                if (useHostSettings) {
                    try {
                        String hostname = id.substring(id.indexOf("://") + 3);
                        Host host = hostDb.getByHostName(hostname);
                        if (host != null) {
                            fiq = new FetchItemQueue(conf, host.getInt("q_mt", maxThreads), host.getLong("q_cd", crawlDelay),
                                    host.getLong("q_mcd", minCrawlDelay));
                        }
                    } catch (IOException e) {
                        LOG.error("Error while trying to access host settings", e);
                    }
                }
                if (fiq == null) {
                    fiq = new FetchItemQueue(conf, maxThreads, crawlDelay, minCrawlDelay);
                }
                queues.put(id, fiq);
            }
            return fiq;
        }

        public synchronized FetchItem getFetchItem() {
            final Iterator<Map.Entry<String, FetchItemQueue>> it = queues.entrySet().iterator();
            while (it.hasNext()) {
                final FetchItemQueue fiq = it.next().getValue();
                if (fiq.getQueueSize() == 0 && fiq.getInProgressSize() == 0) {
                    it.remove();
                    continue;
                }
                final FetchItem fit = fiq.getFetchItem();
                if (fit != null) {
                    totalSize.decrementAndGet();
                    return fit;
                }
            }
            return null;
        }

        public synchronized int checkTimelimit() {
            if (System.currentTimeMillis() >= timelimit && timelimit != -1) {
                return emptyQueues();
            }
            return 0;
        }

        public synchronized void dump() {
            for (final String id : queues.keySet()) {
                final FetchItemQueue fiq = queues.get(id);
                if (fiq.getQueueSize() == 0) {
                    continue;
                }
                LOG.info("* queue: " + id);
                fiq.dump();
            }
        }

        public synchronized int emptyQueues() {
            int count = 0;
            for (String id : queues.keySet()) {
                FetchItemQueue fiq = queues.get(id);
                if (fiq.getQueueSize() == 0) {
                    continue;
                }
                LOG.info("* queue: " + id + " >> dropping! ");
                int deleted = fiq.emptyQueue();
                for (int i = 0; i < deleted; i++) {
                    totalSize.decrementAndGet();
                }
                count += deleted;
            }
            if (totalSize.get() != 0 && queues.size() == 0) {
                totalSize.set(0);
            }
            return count;
        }
    }

    private class FetcherThread extends Thread {
        private final URLFilters urlFilters;
        private final URLNormalizers normalizers;
        private final ProtocolFactory protocolFactory;
        private final long maxCrawlDelay;
        private final boolean byIP;
        private String reprUrl;
        private final Context context;
        private final boolean ignoreExternaLinks;

        public FetcherThread(Context context, int num) {
            this.setDaemon(true);
            this.setName("FetcherThread" + num);
            this.context = context;
            Configuration conf = context.getConfiguration();
            this.urlFilters = new URLFilters(conf);
            this.protocolFactory = new ProtocolFactory(conf);
            this.normalizers = new URLNormalizers(conf, URLNormalizers.SCOPE_FETCHER);
            this.maxCrawlDelay = conf.getInt("fetcher.max.crawl.delay", 30) * 1000;
            this.byIP = conf.getBoolean("fetcher.threads.per.host.by.ip", true);
            this.ignoreExternaLinks = conf.getBoolean("db.ignore.external.links", false);
        }

        @Override
        public void run() {
            activeThreads.incrementAndGet();
            FetchItem fit = null;
            try {
                while (true) {
                    fit = fetchQueues.getFetchItem();
                    if (fit == null) {
                        if (feeder.isAlive() || fetchQueues.getTotalSize() > 0) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug(getName() + "fetchQueues.getFetchItem() was null, spin-waiting ...");
                            }
                            spinWaiting.incrementAndGet();
                            try {
                                Thread.sleep(500);
                            } catch (final Exception e) {
                            }
                            spinWaiting.decrementAndGet();
                            continue;
                        } else {
                            return;
                        }
                    }
                    lastRequestStart.set(System.currentTimeMillis());
                    if (fit.page.getReprUrl() == null) {
                        reprUrl = fit.url;
                    } else {
                        reprUrl = TableUtil.toString(fit.page.getReprUrl());
                    }
                    try {
                        LOG.info("fetching " + fit.url + " (queue crawl delay=" + fetchQueues.getFetchItemQueue(fit.queueID).crawlDelay + "ms)");
                        final Protocol protocol = this.protocolFactory.getProtocol(fit.url);
                        final BaseRobotRules rules = protocol.getRobotRules(fit.url, fit.page);
                        if (!rules.isAllowed(fit.u.toString())) {
                            fetchQueues.finishFetchItem(fit, true);
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Denied by robots.tex: " + fit.url);
                            }
                            output(fit, null, ProtocolStatusUtils.STATUS_ROBOTS_DENIED, CrawlStatus.STATUS_GONE);
                            continue;
                        }
                        if (rules.getCrawlDelay() > 0) {
                            if (rules.getCrawlDelay() > maxCrawlDelay && maxCrawlDelay >= 0) {
                                fetchQueues.finishFetchItem(fit, true);
                                LOG.debug("Crawl-Delay for " + fit.url + " too.long (" + rules.getCrawlDelay() + "), skipping");
                                output(fit, null, ProtocolStatusUtils.STATUS_ROBOTS_DENIED, CrawlStatus.STATUS_GONE);
                                continue;
                            } else {
                                final FetchItemQueue fiq = fetchQueues.getFetchItemQueue(fit.queueID);
                                fiq.crawlDelay = rules.getCrawlDelay();
                                if (LOG.isDebugEnabled()) {
                                    LOG.info("Crawl delay for queue: " + fit.queueID + "" +
                                            " is set to " + fiq.crawlDelay
                                            + " as per robots.txt url: " + fit.url);
                                }
                            }
                        }
                        final ProtocolOutput output = protocol.getProtocolOutput(fit.url, fit.page);
                        final ProtocolStatus status = output.getStatus();
                        final Content content = output.getContent();
                        fetchQueues.finishFetchItem(fit);
                        context.getCounter("FetcherStatus", ProtocolStatusUtils.getName(status.getCode())).increment(1);
                        int length = 0;
                        if (content != null && content.getContent() != null) {
                            length = content.getContent().length;
                        }
                        updateStatus(length);
                        switch (status.getCode()) {
                            case ProtocolStatusCodes.WOULDBLOCK:
                                fetchQueues.addFetchItem(fit);
                                break;
                            case ProtocolStatusCodes.SUCCESS:
                                output(fit, content, status, CrawlStatus.STATUS_FETCHED);
                                break;
                            case ProtocolStatusCodes.MOVED:
                            case ProtocolStatusCodes.TEMP_MOVED:
                                byte code;
                                boolean temp;
                                if (status.getCode() == ProtocolStatusCodes.MOVED) {
                                    code = CrawlStatus.STATUS_REDIR_PERM;
                                    temp = false;
                                } else {
                                    code = CrawlStatus.STATUS_REDIR_TEMP;
                                    temp = true;
                                }
                                final String newUrl = ProtocolStatusUtils.getMessage(status);
                                handleRedirect(fit.url, newUrl, temp, FetcherJob.PROTOCOL_REDIR, fit.page);
                                output(fit, content, status, code);
                                break;
                            case ProtocolStatusCodes.EXCEPTION:
                                logFetchFailure(fit.url, ProtocolStatusUtils.getMessage(status));
                            case ProtocolStatusCodes.RETRY:
                            case ProtocolStatusCodes.BLOCKED:
                                output(fit, null, status, CrawlStatus.STATUS_RETRY);
                                break;
                            case ProtocolStatusCodes.GONE:
                            case ProtocolStatusCodes.NOTFOUND:
                            case ProtocolStatusCodes.ACCESS_DENIED:
                            case ProtocolStatusCodes.ROBOTS_DENIED:
                                output(fit, null, status, CrawlStatus.STATUS_GONE);
                                break;
                            case ProtocolStatusCodes.NOTMODIFIED:
                                output(fit, null, status, CrawlStatus.STATUS_NOTMODIFIED);
                                break;
                            default:
                                if (LOG.isWarnEnabled()) {
                                    LOG.warn("Unknown ProtocolStatus: " + status.getCode());
                                }
                                output(fit, null, status, CrawlStatus.STATUS_RETRY);

                        }
                    } catch (final Throwable t) {
                        fetchQueues.finishFetchItem(fit);
                        LOG.error("Unexpected error for " + fit.url, t);
                        output(fit, null, ProtocolStatusUtils.STATUS_FAILED, CrawlStatus.STATUS_RETRY);
                    }
                }
            } catch (final Throwable e) {
                LOG.error("fetcher throwable caught", e);
            } finally {
                if (fit != null) {
                    fetchQueues.finishFetchItem(fit);
                }
                activeThreads.decrementAndGet();
                LOG.info("-finishing thread " + getName() + ", activeThreads=" + activeThreads);
            }
        }

        private void handleRedirect(String url, String newUrl, boolean temp, String redirType, WebPage page) throws URLFilterException, IOException, InterruptedException {
            newUrl = normalizers.normalize(newUrl, URLNormalizers.SCOPE_FETCHER);
            newUrl = urlFilters.filter(newUrl);
            if (newUrl == null || newUrl.equals(url)) {
                return;
            }
            if (ignoreExternaLinks) {
                String toHost = new URL(newUrl).getHost().toLowerCase();
                String fromHost = new URL(url).getHost().toLowerCase();
                if (toHost == null || !toHost.equals(fromHost)) {
                    return;
                }
            }
            page.getOutlinks().put(new Utf8(newUrl), new Utf8());
            page.getMetadata().put(FetcherJob.REDIRECT_DISCOVERED, TableUtil.YES_VAL);
            reprUrl = URLUtil.chooseRepr(reprUrl, newUrl, temp);
            if (reprUrl == null) {
                LOG.warn("reprUrl==null");
            } else {
                page.setReprUrl(new Utf8(reprUrl));
                if (LOG.isDebugEnabled()) {
                    LOG.debug(" - " + redirType + " redirect to " + reprUrl + " (fetching later)");
                }
            }
        }

        private void updateStatus(int bytesInpage) throws IOException {
            pages.incrementAndGet();
            bytes.addAndGet(bytesInpage);
        }

        private void output(FetchItem fit, Content content, ProtocolStatus pstatus, byte status) throws IOException, InterruptedException {
            fit.page.setStatus((int) status);
            final long prevFetchTime = fit.page.getFetchTime();
            fit.page.setPrevFetchTime(prevFetchTime);
            fit.page.setFetchTime(System.currentTimeMillis());
            if (pstatus != null) {
                fit.page.setProtocolStatus(pstatus);
            }
            if (content != null) {
                fit.page.setContent(ByteBuffer.wrap(content.getContent()));
                fit.page.setContentType(new Utf8(content.getContentType()));
                fit.page.setBaseUrl(new Utf8(content.getBaseUrl()));
            }
            Mark.FETCH_MARK.putMark(fit.page, Mark.GENERATE_MARK.checkMark(fit.page));
            String key = TableUtil.reverseUrl(fit.url);
            if (parse) {
                if (!skipTruncated || (skipTruncated && !ParserJob.isTruncated(fit.url, fit.page))) {
                    parseUtil.process(key, fit.page);
                }
            }
            if (content != null && !storingContent) {
                fit.page.setContent(ByteBuffer.wrap(new byte[0]));
            }
            context.write(key, fit.page);
        }

        private void logFetchFailure(String url, String message) {
            LOG.warn("fetch  of " + url + " failed with: " + message);
            errors.incrementAndGet();
        }
    }

    private static class QueueFeeder extends Thread {
        private final Context context;
        private final FetchItemQueues queues;
        private final int size;
        private Iterator<FetchEntry> currentIter;
        boolean hasMore;
        private long timelimit = -1;

        public QueueFeeder(Context context, FetchItemQueues queues, int size) throws IOException, InterruptedException {
            this.context = context;
            this.queues = queues;
            this.size = size;
            this.setDaemon(true);
            this.setName("QueueFeeder");
            hasMore = context.nextKey();
            if (hasMore) {
                currentIter = context.getValues().iterator();
            }
            timelimit = context.getConfiguration().getLong("fetcher.timelimit", -1);
        }

        @Override
        public void run() {
            int cnt = 0;
            int timelimitcount = 0;
            try {
                while (hasMore) {
                    if (System.currentTimeMillis() >= timelimit && timelimit != -1) {
                        while (currentIter.hasNext()) {
                            currentIter.next();
                            timelimitcount++;
                        }
                        hasMore = context.nextKey();
                        if (hasMore) {
                            currentIter = context.getValues().iterator();
                        }
                        continue;
                    }
                    int feed = size - queues.getTotalSize();
                    if (feed <= 0) {
                        try {
                            Thread.sleep(1000);
                        } catch (final Exception e) {

                        }
                        continue;
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("-feeding " + feed + " input urls ...");
                    }
                    while (feed > 0 && currentIter.hasNext()) {
                        FetchEntry entry = currentIter.next();
                        final String url = TableUtil.unreverseUrl(entry.getKey());
                        queues.addFetchItem(url, entry.getWebPage());
                        feed--;
                        cnt++;
                    }
                    if (currentIter.hasNext()) {
                        continue;
                    }
                    hasMore = context.nextKey();
                    if (hasMore) {
                        currentIter = context.getValues().iterator();
                    }
                }
            } catch (Exception e) {
                LOG.error("QueueFeeder error reading input, record " + cnt, e);
                return;
            }
            LOG.info("QueueFeeder finished: total " + cnt + " records. Hit by time limit :" + timelimitcount);
            context.getCounter("FetcherStatus", "HitByTimeLimit-QueueFeeder").increment(timelimitcount);
        }
    }

    private void reportAndLogStatus(Context context, float actualPages, int actualBytes, int totalSize) throws IOException {
        StringBuilder status = new StringBuilder();
        long elapsed = (System.currentTimeMillis() - start) / 1000;
        status.append(spinWaiting).append("/").append(activeThreads).append(" spinWaiting/active, ");
        status.append(pages).append(" pages, ").append(errors).append(" errors, ");
        status.append(Math.round((((float) pages.get()) * 10) / elapsed) / 10.0).append(" ");
        status.append(Math.round(actualBytes * 10 / 10.0)).append(" pages/s, ");
        status.append(Math.round((((float) bytes.get()) * 8) / 1024) / elapsed).append(" ");
        status.append(Math.round((((float) actualBytes) * 8) / 1024) / elapsed).append(" kb/s, ");
        status.append(totalSize).append(" URLs in ");
        status.append(this.fetchQueues.getQueueCount()).append(" queues");
        String toString = status.toString();
        context.setStatus(toString);
        LOG.info(toString);
    }

    @Override
    public void run(Context context) throws IOException, InterruptedException {
        Configuration conf = context.getConfiguration();
        this.fetchQueues = new FetchItemQueues(conf);
        int threadCount = conf.getInt("fetcher.threads.fetch", 10);
        parse = conf.getBoolean(FetcherJob.PARSE_KEY, false);
        storingContent = conf.getBoolean("fetcher.store.content", true);
        if (parse) {
            skipTruncated = conf.getBoolean(ParserJob.SKIP_TRUNCATED, true);
            parseUtil = new ParseUtil(conf);
        }
        LOG.info("Fether:threads: " + threadCount);
        int maxFeedPerThread = conf.getInt("fetcher.queue.depth.multiplier", 50);
        feeder = new QueueFeeder(context, fetchQueues, threadCount * maxFeedPerThread);
        feeder.start();
        for (int i = 0; i < threadCount; i++) {
            FetcherThread ft = new FetcherThread(context, i);
            fetcherThreads.add(ft);
            ft.start();
        }
        final long timeout = conf.getInt("mapred.task.timeout", 10 * 60 * 1000) / 2;
        float pagesLastSec;
        int bytesLastSec;
        int throughputThresholdCurrentSequence = 0;
        int throughputThresholdPages = conf.getInt("fetcher.throughput.threshold.pages", -1);
        if (LOG.isInfoEnabled()) {
            LOG.info("Fetcher: throughput threshold: " + throughputThresholdPages);
        }
        int throughputThresholdSequence = conf.getInt("fetcher.throughput.threshold.sequence", 5);
        if (LOG.isInfoEnabled()) {
            LOG.info("Fetcher: throughput threshold sequence: " + throughputThresholdSequence);
        }
        long throughputThresholdTimeLimit = conf.getLong("fetcher.throughput.threshold.check.after", -1);
        do {
            pagesLastSec = pages.get();
            bytesLastSec = (int) bytes.get();
            final int secondsToSleep = 5;
            try {
                Thread.sleep(secondsToSleep * 1000);
            } catch (InterruptedException e) {

            }
            pagesLastSec = (pages.get() - pagesLastSec) / secondsToSleep;
            bytesLastSec = ((int) bytes.get() - bytesLastSec) / secondsToSleep;
            int fetchQueuesTotalSize = fetchQueues.getTotalSize();
            reportAndLogStatus(context, pagesLastSec, bytesLastSec, fetchQueuesTotalSize);
            boolean feederAlive = feeder.isAlive();
            if (!feederAlive && fetchQueuesTotalSize < 5) {
                fetchQueues.dump();
            }
            if (!feederAlive) {
                int hitByTimeLimit = fetchQueues.checkTimelimit();
                if (hitByTimeLimit != 0) {
                    context.getCounter("FetcherStatus", "HitByTimeLimit-Queues").increment(hitByTimeLimit);
                }
            }
            if (throughputThresholdTimeLimit < System.currentTimeMillis() && throughputThresholdPages != -1) {
                if (pagesLastSec < throughputThresholdPages) {
                    throughputThresholdSequence++;
                    LOG.warn(Integer.toString(throughputThresholdCurrentSequence)
                            + ": dropping below configured threshold of " + Integer.toString(throughputThresholdPages) + " pages per second");
                    if (throughputThresholdCurrentSequence > throughputThresholdSequence) {
                        LOG.warn("Dropped below threshold too many times in a row, killing!");
                        throughputThresholdPages = -1;
                        int hitByThrougPutThreshold = fetchQueues.emptyQueues();
                        if (hitByThrougPutThreshold != 0) {
                            context.getCounter("FetcherStatus", "hitByThrougputThreshold").increment(hitByThrougPutThreshold);
                        }
                    }
                } else {
                    throughputThresholdCurrentSequence = 0;
                }
            }
            if ((System.currentTimeMillis() - lastRequestStart.get()) > timeout) {
                if (LOG.isWarnEnabled() && activeThreads.get() > 0) {
                    LOG.warn("Aborting with " + activeThreads + " hung threads.");
                    for (int i = 0; i < fetcherThreads.size(); i++) {
                        FetcherThread thread = fetcherThreads.get(i);
                        if (thread.isAlive()) {
                            LOG.warn("Thread #" + i + " hung while processing " + thread.reprUrl);
                            if (LOG.isDebugEnabled()) {
                                StackTraceElement[] stack = thread.getStackTrace();
                                StringBuilder sb = new StringBuilder();
                                sb.append("Stack of thread #").append(i).append(":\n");
                                for (StackTraceElement s : stack) {
                                    sb.append(s.toString()).append('\n');
                                }
                                LOG.debug(sb.toString());
                            }
                        }
                    }
                }
                return;
            }
        } while (activeThreads.get() > 0);
        LOG.info("-activeThreads=" + activeThreads);
    }
}

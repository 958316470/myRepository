package com.nutch.util;

import com.nutch.util.domain.DomainSuffix;
import com.nutch.util.domain.DomainSuffixes;

import java.net.IDN;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.regex.Pattern;

public class URLUtil {

    public static URL resolverURL(URL base, String target) throws MalformedURLException {
        target = target.trim();
        if (target.startsWith("?")) {
            return fixPureQueryTargets(base, target);
        }
        return new URL(base, target);
    }

    static URL fixPureQueryTargets(URL base, String target) throws MalformedURLException {
        if (!target.startsWith("?")) {
            return new URL(base, target);
        }
        String basePath = base.getPath();
        String baseRightMost = "";
        int baseRightMostIdx = basePath.lastIndexOf("/");
        if (baseRightMostIdx != -1) {
            baseRightMost = basePath.substring(baseRightMostIdx + 1);
        }
        if (target.startsWith("?")) {
            target = baseRightMost + target;
        }
        return new URL(base, target);
    }

    private static Pattern IP_PATIERN = Pattern.compile("(\\d{1,2}\\.){3}(\\d{1,3})");

    public static String getDomainName(URL url) {
        DomainSuffixes tlds = DomainSuffixes.getInstance();
        String host = url.getHost();
        if (host.endsWith(".")) {
            host = host.substring(0, host.length() - 1);
        }
        if (IP_PATIERN.matcher(host).matches()) {
            return host;
        }
        int index = 0;
        String candidate = host;
        while (index >= 0) {
            index = candidate.indexOf('.');
            String subCandidate = candidate.substring(index + 1);
            if (tlds.isDomainSuffix(subCandidate)) {
                return candidate;
            }
            candidate = subCandidate;
        }
        return candidate;
    }

    public static String getDomainName(String url) throws MalformedURLException {
        return getDomainName(new URL(url));
    }

    public static boolean isSameDomainName(URL url1, URL url2) {
        return getDomainName(url1).equalsIgnoreCase(getDomainName(url2));
    }

    public static boolean isSameDomainName(String url1, String url2) throws MalformedURLException {
        return isSameDomainName(new URL(url1), new URL(url2));
    }

    public static DomainSuffix getDomainSuffix(URL url) {
        DomainSuffixes tlds = DomainSuffixes.getInstance();
        String host = url.getHost();
        if (IP_PATIERN.matcher(host).matches()) {
            return null;
        }
        int index = 0;
        String canditate = host;
        while (index >= 0) {
            index = canditate.indexOf('.');
            String subCandidate = canditate.substring(index + 1);
            DomainSuffix d = tlds.get(subCandidate);
            if (d != null) {
                return d;
            }
            canditate = subCandidate;
        }
        return null;
    }

    public static DomainSuffix getDomainSuffix(String url) throws MalformedURLException {
        return getDomainSuffix(new URL(url));
    }

    public static String[] getHostBatches(URL url) {
        String host = url.getHost();
        if (IP_PATIERN.matcher(host).matches()) {
            return new String[]{host};
        }
        return host.split("\\.");
    }

    public static String[] getHostBatches(String url) throws MalformedURLException {
        return getHostBatches(new URL(url));
    }

    public static String chooseRepr(String src, String dst, boolean temp) {
        URL srcUrl;
        URL dstUrl;
        try {
            srcUrl = new URL(src);
            dstUrl = new URL(dst);
        } catch (MalformedURLException e) {
            return dst;
        }

        String srcDomain = URLUtil.getDomainName(srcUrl);
        String dstDomain = URLUtil.getDomainName(dstUrl);
        String srcHost = srcUrl.getHost();
        String dstHost = dstUrl.getHost();
        String srcFile = srcUrl.getFile();
        String dstFile = dstUrl.getFile();

        boolean srcRoot = (srcFile.equals("/") || srcFile.length() == 0);
        boolean destRoot = (dstFile.equals("/") || dstFile.length() == 0);
        if (!srcDomain.equals(dstDomain)) {
            return dst;
        }
        if (!temp) {
            if (srcRoot) {
                return src;
            } else {
                return dst;
            }
        } else {
            if (srcRoot && !destRoot) {
                return src;
            } else if (!srcRoot && destRoot) {
                return dst;
            } else if (!srcRoot && !destRoot && (srcHost.equals(dstHost))) {
                int numSrcPaths = srcFile.split("/").length;
                int numDstPaths = dstFile.split("/").length;
                if (numSrcPaths != numDstPaths) {
                    return (numDstPaths < numSrcPaths ? dst : src);
                } else {
                    int srcPathLength = srcFile.length();
                    int dstPathLength = dstFile.length();
                    return (dstPathLength < srcPathLength ? dst : src);
                }
            } else {
                int numSrcSubs = srcHost.split("\\.").length;
                int numDstSubs = dstHost.split("\\.").length;
                return (numDstSubs < numSrcSubs ? dst : src);
            }
        }
    }

    public static String getHost(String url) {
        try {
            return new URL(url).getHost().toLowerCase();
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public static String getPage(String url) {
        try {
            url = url.toLowerCase();
            String queryStr = new URL(url).getQuery();
            return (queryStr != null) ? url.replace("?" + queryStr, "") : url;
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public static String toASCII(String url) {
        try {
            URL u = new URL(url);
            String host = u.getHost();
            if (host == null || host.isEmpty()) {
                return url;
            }
            URI p = new URI(u.getProtocol(), u.getUserInfo(), IDN.toASCII(host), u.getPort(), u.getPath(), u.getQuery(), u.getRef());
            return p.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static String toUNICODE(String url) {
        try {
            URL u = new URL(url);
            String host = u.getHost();
            if (host == null || host.isEmpty()) {
                return url;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(u.getProtocol());
            sb.append("://");
            if (u.getUserInfo() != null) {
                sb.append(u.getUserInfo());
                sb.append('@');
            }
            sb.append(IDN.toUnicode(host));
            if (u.getPort() != -1) {
                sb.append(':');
                sb.append(u.getPort());
            }
            sb.append(u.getFile());
            if (u.getRef() != null) {
                sb.append('#');
                sb.append(u.getRef());
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage : URLUtil <url>");
            return;
        }
        String url = args[0];
        try {
            System.out.println(URLUtil.getDomainName(new URL(url)));
        }catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}

package com.nutch.storage;

import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificRecord;
import org.apache.gora.persistency.Persistent;
import org.apache.gora.persistency.impl.DirtyMapWrapper;
import org.apache.gora.persistency.impl.PersistentBase;

import java.nio.ByteBuffer;
import java.util.Map;

public class WebPage extends PersistentBase implements SpecificRecord, Persistent {
    public static final Schema SCHEMA$ = new Schema.Parser().parse(("{\"type\":\"record\",\"name\":\"WebPage\",\"namespace\":\"org.apache.nutch.storage\",\"doc\":\"WebPage is the primary data structure in Nutch representing crawl data for a given WebPage at some point in time\",\"fields\":[{\"name\":\"baseUrl\",\"type\":[\"null\",\"string\"],\"doc\":\"The original associated with this WebPage.\",\"default\":null},{\"name\":\"status\",\"type\":\"int\",\"doc\":\"A crawl status associated with the WebPage, can be of value STATUS_UNFETCHED - WebPage was not fetched yet, STATUS_FETCHED - WebPage was successfully fetched, STATUS_GONE - WebPage no longer exists, STATUS_REDIR_TEMP - WebPage temporarily redirects to other page, STATUS_REDIR_PERM - WebPage permanently redirects to other page, STATUS_RETRY - Fetching unsuccessful, needs to be retried e.g. transient errors and STATUS_NOTMODIFIED - fetching successful - page is not modified\",\"default\":0},{\"name\":\"fetchTime\",\"type\":\"long\",\"doc\":\"The system time in milliseconds for when the page was fetched.\",\"default\":0},{\"name\":\"prevFetchTime\",\"type\":\"long\",\"doc\":\"The system time in milliseconds for when the page was last fetched if it was previously fetched which can be used to calculate time delta within a fetching schedule implementation\",\"default\":0},{\"name\":\"fetchInterval\",\"type\":\"int\",\"doc\":\"The default number of seconds between re-fetches of a page. The default is considered as 30 days unless a custom fetch schedle is implemented.\",\"default\":0},{\"name\":\"retriesSinceFetch\",\"type\":\"int\",\"doc\":\"The number of retried attempts at fetching the WebPage since it was last successfully fetched.\",\"default\":0},{\"name\":\"modifiedTime\",\"type\":\"long\",\"doc\":\"The system time in milliseconds for when this WebPage was modified by the WebPage author, if this is not available we default to the server for this information. This is important to understand the changing nature of the WebPage.\",\"default\":0},{\"name\":\"prevModifiedTime\",\"type\":\"long\",\"doc\":\"The system time in milliseconds for when this WebPage was previously modified by the author, if this is not available then we default to the server for this information. This is important to understand the changing nature of a WebPage.\",\"default\":0},{\"name\":\"protocolStatus\",\"type\":[\"null\",{\"type\":\"record\",\"name\":\"ProtocolStatus\",\"doc\":\"A nested container representing data captured from web server responses.\",\"fields\":[{\"name\":\"code\",\"type\":\"int\",\"doc\":\"A protocol response code which can be one of SUCCESS - content was retrieved without errors, FAILED - Content was not retrieved. Any further errors may be indicated in args, PROTO_NOT_FOUND - This protocol was not found. Application may attempt to retry later, GONE - Resource is gone, MOVED - Resource has moved permanently. New url should be found in args, TEMP_MOVED - Resource has moved temporarily. New url should be found in args., NOTFOUND - Resource was not found, RETRY - Temporary failure. Application may retry immediately., EXCEPTION - Unspecified exception occured. Further information may be provided in args., ACCESS_DENIED - Access denied - authorization required, but missing/incorrect., ROBOTS_DENIED - Access denied by robots.txt rules., REDIR_EXCEEDED - Too many redirects., NOTFETCHING - Not fetching., NOTMODIFIED - Unchanged since the last fetch., WOULDBLOCK - Request was refused by protocol plugins, because it would block. The expected number of milliseconds to wait before retry may be provided in args., BLOCKED - Thread was blocked http.max.delays times during fetching.\",\"default\":0},{\"name\":\"args\",\"type\":{\"type\":\"array\",\"items\":\"string\"},\"doc\":\"Optional arguments supplied to compliment and/or justify the response code.\",\"default\":[]},{\"name\":\"lastModified\",\"type\":\"long\",\"doc\":\"A server reponse indicating when this page was last modified, this can be unreliable at times hence this is used as a default fall back value for the preferred 'modifiedTime' and 'preModifiedTime' obtained from the WebPage itself.\",\"default\":0}]}],\"default\":null},{\"name\":\"content\",\"type\":[\"null\",\"bytes\"],\"doc\":\"The entire raw document content e.g. raw XHTML\",\"default\":null},{\"name\":\"contentType\",\"type\":[\"null\",\"string\"],\"doc\":\"The type of the content contained within the document itself. ContentType is an alias for MimeType. Historically, this parameter was only called MimeType, but since this is actually the value included in the HTTP Content-Type header, it can also include the character set encoding, which makes it more than just a MimeType specification. If MimeType is specified e.g. not None, that value is used. Otherwise, ContentType is used. If neither is given, the DEFAULT_CONTENT_TYPE setting is used.\",\"default\":null},{\"name\":\"prevSignature\",\"type\":[\"null\",\"bytes\"],\"doc\":\"An implementation of a WebPage's previous signature from which it can be identified and referenced at any point in time. This can be used to uniquely identify WebPage deltas based on page fingerprints.\",\"default\":null},{\"name\":\"signature\",\"type\":[\"null\",\"bytes\"],\"doc\":\"An implementation of a WebPage's signature from which it can be identified and referenced at any point in time. This is essentially the WebPage's fingerprint represnting its state for any point in time.\",\"default\":null},{\"name\":\"title\",\"type\":[\"null\",\"string\"],\"doc\":\"The title of the WebPage.\",\"default\":null},{\"name\":\"text\",\"type\":[\"null\",\"string\"],\"doc\":\"The textual content of the WebPage devoid from native markup.\",\"default\":null},{\"name\":\"parseStatus\",\"type\":[\"null\",{\"type\":\"record\",\"name\":\"ParseStatus\",\"doc\":\"A nested container representing parse status data captured from invocation of parsers on fetch of a WebPage\",\"fields\":[{\"name\":\"majorCode\",\"type\":\"int\",\"doc\":\"Major parsing status' including NOTPARSED (Parsing was not performed), SUCCESS (Parsing succeeded), FAILED (General failure. There may be a more specific error message in arguments.)\",\"default\":0},{\"name\":\"minorCode\",\"type\":\"int\",\"doc\":\"Minor parsing status' including SUCCESS_OK - Successful parse devoid of anomalies or issues, SUCCESS_REDIRECT - Parsed content contains a directive to redirect to another URL. The target URL can be retrieved from the arguments., FAILED_EXCEPTION - Parsing failed. An Exception occured which may be retrieved from the arguments., FAILED_TRUNCATED - Parsing failed. Content was truncated, but the parser cannot handle incomplete content., FAILED_INVALID_FORMAT - Parsing failed. Invalid format e.g. the content may be corrupted or of wrong type., FAILED_MISSING_PARTS - Parsing failed. Other related parts of the content are needed to complete parsing. The list of URLs to missing parts may be provided in arguments. The Fetcher may decide to fetch these parts at once, then put them into Content.metadata, and supply them for re-parsing., FAILED_MISING_CONTENT - Parsing failed. There was no content to be parsed - probably caused by errors at protocol stage.\",\"default\":0},{\"name\":\"args\",\"type\":{\"type\":\"array\",\"items\":\"string\"},\"doc\":\"Optional arguments supplied to compliment and/or justify the parse status code.\",\"default\":[]}]}],\"default\":null},{\"name\":\"score\",\"type\":\"float\",\"doc\":\"A score used to determine a WebPage's relevance within the web graph it is part of. This score may change over time based on graph characteristics.\",\"default\":0},{\"name\":\"reprUrl\",\"type\":[\"null\",\"string\"],\"doc\":\"In the case where we are given two urls, a source and a destination of a redirect, we should determine and persist the representative url. The logic used to determine this is based largely on Yahoo!'s Slurp Crawler\",\"default\":null},{\"name\":\"headers\",\"type\":{\"type\":\"map\",\"values\":[\"null\",\"string\"]},\"doc\":\"Header information returned from the web server used to server the content which is subsequently fetched from. This includes keys such as TRANSFER_ENCODING, CONTENT_ENCODING, CONTENT_LANGUAGE, CONTENT_LENGTH, CONTENT_LOCATION, CONTENT_DISPOSITION, CONTENT_MD5, CONTENT_TYPE, LAST_MODIFIED and LOCATION.\",\"default\":{}},{\"name\":\"outlinks\",\"type\":{\"type\":\"map\",\"values\":[\"null\",\"string\"]},\"doc\":\"Embedded hyperlinks which direct outside of the current domain.\",\"default\":{}},{\"name\":\"inlinks\",\"type\":{\"type\":\"map\",\"values\":[\"null\",\"string\"]},\"doc\":\"Embedded hyperlinks which link to pages within the current domain.\",\"default\":{}},{\"name\":\"markers\",\"type\":{\"type\":\"map\",\"values\":[\"null\",\"string\"]},\"doc\":\"Markers flags which represent user and machine decisions which have affected influenced a WebPage's current state. Markers can be system specific and user machine driven in nature. They are assigned to a WebPage on a job-by-job basis and thier values indicative of what actions should be associated with a WebPage.\",\"default\":{}},{\"name\":\"metadata\",\"type\":{\"type\":\"map\",\"values\":[\"null\",\"bytes\"]},\"doc\":\"A multi-valued metadata container used for storing everything from structured WebPage characterists, to ad-hoc extraction and metadata augmentation for any given WebPage.\",\"default\":{}},{\"name\":\"batchId\",\"type\":[\"null\",\"string\"],\"doc\":\"A batchId that this WebPage is assigned to. WebPage's are fetched in batches, called fetchlists. Pages are partitioned but can always be associated and fetched alongside pages of similar value (within a crawl cycle) based on batchId.\",\"default\":null}]}"));


    @Override
    public int getFieldsCount() {
        return WebPage._ALL_FIELDS.length;
    }

    @Override
    public Schema getSchema() {
        return SCHEMA$;
    }

    @Override
    public Object get(int field$) {
        switch (field$) {
            case 0:
                return baseUrl;
            case 1:
                return status;
            case 2:
                return fetchTime;
            case 3:
                return prevFetchTime;
            case 4:
                return fetchInterval;
            case 5:
                return retriesSinceFetch;
            case 6:
                return modifiedTime;
            case 7:
                return prevModifiedTime;
            case 8:
                return protocolStatus;
            case 9:
                return content;
            case 10:
                return contentType;
            case 11:
                return prevSignature;
            case 12:
                return signature;
            case 13:
                return title;
            case 14:
                return text;
            case 15:
                return parseStatus;
            case 16:
                return score;
            case 17:
                return reprUrl;
            case 18:
                return headers;
            case 19:
                return outlinks;
            case 20:
                return inlinks;
            case 21:
                return markers;
            case 22:
                return metadata;
            case 23:
                return batchId;
            default:
                throw new org.apache.avro.AvroRuntimeException("Bad index");
        }
    }

    @Override
    public void put(int field$, Object value) {
        switch (field$) {
            case 0:
                baseUrl = (CharSequence) value;
                break;
            case 1:
                status = (Integer) value;
                break;
            case 2:
                fetchTime = (Long) value;
                break;
            case 3:
                prevFetchTime = (Long) value;
                break;
            case 4:
                fetchInterval = (Integer) value;
                break;
            case 5:
                retriesSinceFetch = (Integer) value;
                break;
            case 6:
                modifiedTime = (Long) value;
                break;
            case 7:
                prevModifiedTime = (Long) value;
                break;
            case 8:
                protocolStatus = (ProtocolStatus) value;
                break;
            case 9:
                content = (ByteBuffer) value;
                break;
            case 10:
                contentType = (CharSequence) value;
                break;
            case 11:
                prevSignature = (ByteBuffer) value;
                break;
            case 12:
                signature = (ByteBuffer) value;
                break;
            case 13:
                title = (CharSequence) value;
                break;
            case 14:
                text = (CharSequence) value;
                break;
            case 15:
                parseStatus = (ParseStatus) value;
                break;
            case 16:
                score = (Float) value;
                break;
            case 17:
                reprUrl = (CharSequence) value;
                break;
            case 18:
                headers = (Map<CharSequence, CharSequence>) ((value instanceof org.apache.gora.persistency.Dirtyable) ? value : new DirtyMapWrapper((Map) value));
                break;
            case 19:
                outlinks = (Map<CharSequence, CharSequence>) ((value instanceof org.apache.gora.persistency.Dirtyable) ? value : new DirtyMapWrapper((Map) value));
                break;
            case 20:
                inlinks = (Map<CharSequence, CharSequence>) ((value instanceof org.apache.gora.persistency.Dirtyable) ? value : new DirtyMapWrapper((Map) value));
                break;
            case 21:
                markers = (Map<CharSequence, CharSequence>) ((value instanceof org.apache.gora.persistency.Dirtyable) ? value : new DirtyMapWrapper((Map) value));
                break;
            case 22:
                metadata = (Map<CharSequence, ByteBuffer>) ((value instanceof org.apache.gora.persistency.Dirtyable) ? value : new DirtyMapWrapper((Map) value));
                break;
            case 23:
                batchId = (CharSequence) value;
                break;
            default:
                throw new org.apache.avro.AvroRuntimeException("Bad index");
        }
    }

    @Override
    public Tombstone getTombstone() {
        return TOMBSTONE;
    }

    @Override
    public Persistent newInstance() {
        return newBuilder().build();
    }

    public static enum Field {
        BASE_URL(0, "baseurl"), STATUS(1, "status"), FETCH_TIME(2, "fetchTime"),
        PREV_FETCH_TIME(3, "prevFetchTime"), FETCH_INTERVAL(4, "fetchInterval"),
        RETRIES_SINCE_FETCH(5, "retriesSinceFetch"), MODIFIED_TIME(6, "modifiedTime"),
        PREV_MODIFIED_TIME(7, "prevModifiedTime"), PROTOCOL_STATUS(8, "protocolStatus"),
        CONTENT(9, "content"), CONTENT_TYPE(10, "contentType"), PREV_SIGNATURE(11, "prevSignature"),
        SIGNATURE(12, "signature"), TITLE(13, "title"), TEXT(14, "text"), PARSE_STATUS(15, "parseStatus"),
        SCORE(16, "score"), REPR_URL(17, "reprUrl"), HEADERS(18, "headers"), OUTLINKS(19, "outlinks"), INLINKS(20, "inlinks"),
        MARKERS(21, "markers"), METADATA(22, "metadata"), BATCH_ID(23, "batchId");

        private int index;
        private String name;

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }

        Field(int index, String name) {
            this.index = index;
            this.name = name;
        }
    }

    ;

    public static final String[] _ALL_FIELDS = {"baseUrl", "status", "fetchTime", "prevFetchTime", "fetchInterval",
            "retriesSinceFetch", "modifiedTime", "prevModifiedTime", "protocolStatus", "content", "contentType", "prevSignature", "signature", "title",
            "text", "parseStatus", "score", "reprUrl", "headers", "outlinks", "inlinks", "markers", "metadata", "batchId"};

    private CharSequence baseUrl;

    private int status;

    private long fetchTime;

    private long prevFetchTime;

    private int fetchInterval;

    private int retriesSinceFetch;

    private long modifiedTime;

    private long prevModifiedTime;

    private ProtocolStatus protocolStatus;

    private ByteBuffer content;

    private CharSequence contentType;

    private ByteBuffer prevSignature;

    private ByteBuffer signature;

    private CharSequence title;

    private CharSequence text;

    private ParseStatus parseStatus;

    private float score;

    private CharSequence reprUrl;

    private Map<CharSequence, CharSequence> headers;

    private Map<CharSequence, CharSequence> outlinks;

    private Map<CharSequence, CharSequence> inlinks;

    private Map<CharSequence, CharSequence> markers;

    private Map<CharSequence, ByteBuffer> metadata;

    private CharSequence batchId;

    public CharSequence getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(CharSequence value) {
        this.baseUrl = value;
        setDirty(0);
    }

    public boolean isBaseUrlDirty(CharSequence value) {
        return isDirty(0);
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer value) {
        this.status = value;
        setDirty(1);
    }

    public boolean isStatusDirty(Integer value) {
        return isDirty(1);
    }

    public Long getFetchTime() {
        return fetchTime;
    }

    public void setFetchTime(Long fetchTime) {
        this.fetchTime = fetchTime;
        setDirty(2);
    }

    public boolean isFetchTimeDirty(Long value) {
        return isDirty(2);
    }

    public Long getPrevFetchTime() {
        return prevFetchTime;
    }

    public void setPrevFetchTime(Long prevFetchTime) {
        this.prevFetchTime = prevFetchTime;
        setDirty(3);
    }

    public boolean isPrevFetchTimeDirty(Long prevFetchTime) {
        return isDirty(3);
    }

    public Integer getFetchInterval() {
        return fetchInterval;
    }

    public void setFetchInterval(Integer fetchInterval) {
        this.fetchInterval = fetchInterval;
        setDirty(4);
    }

    public boolean isFetchIntervalDirty(Integer value) {
        return isDirty(4);
    }

    public Integer getRetriesSinceFetch() {
        return retriesSinceFetch;
    }

    public void setRetriesSinceFetch(Integer retriesSinceFetch) {
        this.retriesSinceFetch = retriesSinceFetch;
        setDirty(5);
    }

    public boolean isRetriesSinceFetchDirty(Integer retriesSinceFetch) {
        return isDirty(5);
    }


    public Long getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Long modifiedTime) {
        this.modifiedTime = modifiedTime;
        setDirty(6);
    }

    public boolean isModifiedTimeDirty(Long modifiedTime) {
        return isDirty(6);
    }


    public Long getPrevModifiedTime() {
        return prevModifiedTime;
    }

    public void setPrevModifiedTime(Long prevModifiedTime) {
        this.prevModifiedTime = prevModifiedTime;
        setDirty(7);
    }

    public boolean isPrevModifiedTimeDirty(Long prevModifiedTime) {
        return isDirty(7);
    }


    public ProtocolStatus getProtocolStatus() {
        return protocolStatus;
    }

    public void setProtocolStatus(ProtocolStatus protocolStatus) {
        this.protocolStatus = protocolStatus;
        setDirty(8);
    }

    public boolean isProtocolStatusDirty(ProtocolStatus protocolStatus) {
        return isDirty(8);
    }


    public ByteBuffer getContent() {
        return content;
    }

    public void setContent(ByteBuffer content) {
        this.content = content;
        setDirty(9);
    }

    public boolean isContentDirty(ByteBuffer content) {
        return isDirty(9);
    }


    public CharSequence getContentType() {
        return contentType;
    }

    public void setContentType(CharSequence contentType) {
        this.contentType = contentType;
        setDirty(10);
    }

    public boolean isContentTypeDirty(CharSequence contentType) {
        return isDirty(10);
    }


    public ByteBuffer getPrevSignature() {
        return prevSignature;
    }

    public void setPrevSignature(ByteBuffer prevSignature) {
        this.prevSignature = prevSignature;
        setDirty(11);
    }

    public boolean isPrevSignatureDirty(ByteBuffer prevSignature) {
        return isDirty(11);
    }


    public ByteBuffer getSignature() {
        return signature;
    }

    public void setSignature(ByteBuffer signature) {
        this.signature = signature;
        setDirty(12);
    }

    public boolean isSignatureDirty(ByteBuffer signature) {
        return isDirty(12);
    }


    public CharSequence getTitle() {
        return title;
    }

    public void setTitle(CharSequence title) {
        this.title = title;
        setDirty(13);
    }

    public boolean isTitleDirty(CharSequence title) {
        return isDirty(13);
    }


    public CharSequence getText() {
        return text;
    }

    public void setText(CharSequence text) {
        this.text = text;
        setDirty(14);
    }

    public boolean isTextDirty(CharSequence text) {
        return isDirty(14);
    }


    public ParseStatus getParseStatus() {
        return parseStatus;
    }

    public void setParseStatus(ParseStatus parseStatus) {
        this.parseStatus = parseStatus;
        setDirty(15);
    }

    public boolean isParseStatusDirty(ParseStatus parseStatus) {
        return isDirty(15);
    }


    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
        setDirty(16);
    }

    public boolean isScoreDirty(Float score) {
        return isDirty(16);
    }


    public CharSequence getReprUrl() {
        return reprUrl;
    }

    public void setReprUrl(CharSequence reprUrl) {
        this.reprUrl = reprUrl;
        setDirty(17);
    }

    public boolean isReprUrlDirty(CharSequence reprUrl) {
        return isDirty(17);
    }

    public Map<CharSequence, CharSequence> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<CharSequence, CharSequence> headers) {
        this.headers = (headers instanceof org.apache.gora.persistency.Dirtyable) ? headers
                : new org.apache.gora.persistency.impl.DirtyMapWrapper(headers);
        setDirty(18);
    }

    public boolean isHeadersDirty(Map<CharSequence, CharSequence> headers) {
        return isDirty(18);
    }


    public Map<CharSequence, CharSequence> getOutlinks() {
        return outlinks;
    }

    public void setOutlinks(Map<CharSequence, CharSequence> outlinks) {
        this.outlinks = (outlinks instanceof org.apache.gora.persistency.Dirtyable) ? outlinks
                : new org.apache.gora.persistency.impl.DirtyMapWrapper(outlinks);
        setDirty(19);
    }

    public boolean isOutlinksDirty(Map<CharSequence, CharSequence> outlinks) {
        return isDirty(19);
    }


    public Map<CharSequence, CharSequence> getInlinks() {
        return inlinks;
    }

    public void setInlinks(Map<CharSequence, CharSequence> inlinks) {
        this.inlinks = (inlinks instanceof org.apache.gora.persistency.Dirtyable) ? inlinks
                : new org.apache.gora.persistency.impl.DirtyMapWrapper(inlinks);
        setDirty(20);
    }

    public boolean isInlinksDirty(Map<CharSequence, CharSequence> inlinks) {
        return isDirty(20);
    }


    public Map<CharSequence, CharSequence> getMarkers() {
        return markers;
    }

    public void setMarkers(Map<CharSequence, CharSequence> markers) {
        this.markers = (markers instanceof org.apache.gora.persistency.Dirtyable) ? markers
                : new org.apache.gora.persistency.impl.DirtyMapWrapper(markers);
        setDirty(21);
    }

    public boolean isMarkersDirty(Map<CharSequence, CharSequence> markers) {
        return isDirty(21);
    }


    public Map<CharSequence, ByteBuffer> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<CharSequence, ByteBuffer> metadata) {
        this.metadata = (metadata instanceof org.apache.gora.persistency.Dirtyable) ? metadata
                : new org.apache.gora.persistency.impl.DirtyMapWrapper(metadata);
        setDirty(22);
    }

    public boolean isMetadataDirty(Map<CharSequence, ByteBuffer> metadata) {
        return isDirty(22);
    }


    public CharSequence getBatchId() {
        return batchId;
    }

    public void setBatchId(CharSequence batchId) {
        this.batchId = batchId;
        setDirty(23);
    }

    public boolean isBatchIdDirty(CharSequence batchId) {
        return isDirty(23);
    }

    public static com.nutch.storage.WebPage.Builder newBuilder() {
        return new com.nutch.storage.WebPage.Builder();
    }

    public static com.nutch.storage.WebPage.Builder newBuilder(com.nutch.storage.WebPage.Builder other) {
        return new com.nutch.storage.WebPage.Builder(other);
    }

    public static com.nutch.storage.WebPage.Builder newBuilder(com.nutch.storage.WebPage other) {
        return new com.nutch.storage.WebPage.Builder(other);
    }


    public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<WebPage> implements
            org.apache.avro.data.RecordBuilder<WebPage> {

        private CharSequence baseUrl;

        private int status;

        private long fetchTime;

        private long prevFetchTime;

        private int fetchInterval;

        private int retriesSinceFetch;

        private long modifiedTime;

        private long prevModifiedTime;

        private ProtocolStatus protocolStatus;

        private ByteBuffer content;

        private CharSequence contentType;

        private ByteBuffer prevSignature;

        private ByteBuffer signature;

        private CharSequence title;

        private CharSequence text;

        private ParseStatus parseStatus;

        private float score;

        private CharSequence reprUrl;

        private Map<CharSequence, CharSequence> headers;

        private Map<CharSequence, CharSequence> outlinks;

        private Map<CharSequence, CharSequence> inlinks;

        private Map<CharSequence, CharSequence> markers;

        private Map<CharSequence, ByteBuffer> metadata;

        private CharSequence batchId;

        private Builder() {
            super(WebPage.SCHEMA$);
        }

        private Builder(WebPage.Builder other) {
            super(other);
        }

        private Builder(WebPage other) {
            super(WebPage.SCHEMA$);
            if (isValidValue(fields()[0], other.baseUrl)) {
                this.baseUrl = (CharSequence) data().deepCopy(fields()[0].schema(), other.baseUrl);
                fieldSetFlags()[0] = true;
            }


            if (isValidValue(fields()[1], other.status)) {
                this.status = (int) data().deepCopy(fields()[1].schema(), other.status);
                fieldSetFlags()[1] = true;
            }


            if (isValidValue(fields()[2], other.fetchTime)) {
                this.fetchTime = (long) data().deepCopy(fields()[2].schema(), other.fetchTime);
                fieldSetFlags()[2] = true;
            }


            if (isValidValue(fields()[3], other.prevFetchTime)) {
                this.prevFetchTime = (long) data().deepCopy(fields()[3].schema(), other.prevFetchTime);
                fieldSetFlags()[3] = true;
            }


            if (isValidValue(fields()[4], other.fetchInterval)) {
                this.fetchInterval = (int) data().deepCopy(fields()[4].schema(), other.fetchInterval);
                fieldSetFlags()[4] = true;
            }


            if (isValidValue(fields()[5], other.retriesSinceFetch)) {
                this.retriesSinceFetch = (int) data().deepCopy(fields()[5].schema(), other.retriesSinceFetch);
                fieldSetFlags()[5] = true;
            }


            if (isValidValue(fields()[6], other.modifiedTime)) {
                this.modifiedTime = (long) data().deepCopy(fields()[6].schema(), other.modifiedTime);
                fieldSetFlags()[6] = true;
            }


            if (isValidValue(fields()[7], other.prevModifiedTime)) {
                this.prevModifiedTime = (long) data().deepCopy(fields()[7].schema(), other.prevModifiedTime);
                fieldSetFlags()[7] = true;
            }


            if (isValidValue(fields()[8], other.protocolStatus)) {
                this.protocolStatus = (ProtocolStatus) data().deepCopy(fields()[8].schema(), other.protocolStatus);
                fieldSetFlags()[8] = true;
            }


            if (isValidValue(fields()[9], other.content)) {
                this.content = (ByteBuffer) data().deepCopy(fields()[9].schema(), other.content);
                fieldSetFlags()[9] = true;
            }


            if (isValidValue(fields()[10], other.contentType)) {
                this.contentType = (CharSequence) data().deepCopy(fields()[10].schema(), other.contentType);
                fieldSetFlags()[10] = true;
            }


            if (isValidValue(fields()[11], other.prevSignature)) {
                this.prevSignature = (ByteBuffer) data().deepCopy(fields()[11].schema(), other.prevSignature);
                fieldSetFlags()[11] = true;
            }


            if (isValidValue(fields()[12], other.signature)) {
                this.signature = (ByteBuffer) data().deepCopy(fields()[12].schema(), other.signature);
                fieldSetFlags()[12] = true;
            }


            if (isValidValue(fields()[13], other.title)) {
                this.title = (CharSequence) data().deepCopy(fields()[13].schema(), other.title);
                fieldSetFlags()[13] = true;
            }


            if (isValidValue(fields()[14], other.text)) {
                this.text = (CharSequence) data().deepCopy(fields()[14].schema(), other.text);
                fieldSetFlags()[14] = true;
            }


            if (isValidValue(fields()[15], other.parseStatus)) {
                this.parseStatus = (ParseStatus) data().deepCopy(fields()[15].schema(), other.parseStatus);
                fieldSetFlags()[15] = true;
            }


            if (isValidValue(fields()[16], other.score)) {
                this.score = (float) data().deepCopy(fields()[16].schema(), other.score);
                fieldSetFlags()[16] = true;
            }


            if (isValidValue(fields()[17], other.reprUrl)) {
                this.reprUrl = (CharSequence) data().deepCopy(fields()[17].schema(), other.reprUrl);
                fieldSetFlags()[17] = true;
            }


            if (isValidValue(fields()[18], other.headers)) {
                this.headers = (Map<CharSequence, CharSequence>) data().deepCopy(fields()[18].schema(), other.headers);
                fieldSetFlags()[18] = true;
            }


            if (isValidValue(fields()[19], other.outlinks)) {
                this.outlinks = (Map<CharSequence, CharSequence>) data().deepCopy(fields()[19].schema(), other.outlinks);
                fieldSetFlags()[19] = true;
            }


            if (isValidValue(fields()[20], other.inlinks)) {
                this.inlinks = (Map<CharSequence, CharSequence>) data().deepCopy(fields()[20].schema(), other.inlinks);
                fieldSetFlags()[20] = true;
            }


            if (isValidValue(fields()[21], other.markers)) {
                this.markers = (Map<CharSequence, CharSequence>) data().deepCopy(fields()[21].schema(), other.markers);
                fieldSetFlags()[21] = true;
            }


            if (isValidValue(fields()[22], other.metadata)) {
                this.metadata = (Map<CharSequence, ByteBuffer>) data().deepCopy(fields()[22].schema(), other.metadata);
                fieldSetFlags()[22] = true;
            }


            if (isValidValue(fields()[23], other.batchId)) {
                this.batchId = (CharSequence) data().deepCopy(fields()[23].schema(), other.batchId);
                fieldSetFlags()[23] = true;
            }
        }

        public CharSequence getBaseUrl() {
            return baseUrl;
        }

        public com.nutch.storage.WebPage.Builder setBaseUrl(CharSequence baseUrl) {
            validate(fields()[0], baseUrl);
            this.baseUrl = baseUrl;
            fieldSetFlags()[0] = true;
            return this;
        }

        public boolean hasBaseUrl() {
            return fieldSetFlags()[0];
        }

        public com.nutch.storage.WebPage.Builder clearBaseUrl() {
            baseUrl = null;
            fieldSetFlags()[0] = false;
            return this;
        }


        public int getStatus() {
            return status;
        }

        public com.nutch.storage.WebPage.Builder setStatus(Integer status) {
            validate(fields()[1], status);
            this.status = status;
            fieldSetFlags()[1] = true;
            return this;
        }

        public boolean hasStatus() {
            return fieldSetFlags()[1];
        }

        public com.nutch.storage.WebPage.Builder clearStatus() {
            fieldSetFlags()[1] = false;
            return this;
        }


        public Long getFetchTime() {
            return fetchTime;
        }

        public com.nutch.storage.WebPage.Builder setFetchTime(Long fetchTime) {
            validate(fields()[2], fetchTime);
            this.fetchTime = fetchTime;
            fieldSetFlags()[2] = true;
            return this;
        }

        public boolean hasFetchTime() {
            return fieldSetFlags()[2];
        }

        public com.nutch.storage.WebPage.Builder clearFetchTime() {
            fieldSetFlags()[2] = false;
            return this;
        }


        public Long getPrevFetchTime() {
            return prevFetchTime;
        }

        public com.nutch.storage.WebPage.Builder setPrevFetchTime(Long prevFetchTime) {
            validate(fields()[3], prevFetchTime);
            this.prevFetchTime = prevFetchTime;
            fieldSetFlags()[3] = true;
            return this;
        }

        public boolean hasPrevFetchTime() {
            return fieldSetFlags()[3];
        }

        public com.nutch.storage.WebPage.Builder clearPrevFetchTime() {
            fieldSetFlags()[3] = false;
            return this;
        }


        public Integer getFetchInterval() {
            return fetchInterval;
        }

        public com.nutch.storage.WebPage.Builder setFetchInterval(Integer fetchInterval) {
            validate(fields()[4], fetchInterval);
            this.fetchInterval = fetchInterval;
            fieldSetFlags()[4] = true;
            return this;
        }

        public boolean hasFetchInterval() {
            return fieldSetFlags()[4];
        }

        public com.nutch.storage.WebPage.Builder clearFetchInterval() {
            fieldSetFlags()[4] = false;
            return this;
        }


        public Integer getRetriesSinceFetch() {
            return retriesSinceFetch;
        }

        public com.nutch.storage.WebPage.Builder setRetriesSinceFetch(Integer retriesSinceFetch) {
            validate(fields()[5], retriesSinceFetch);
            this.retriesSinceFetch = retriesSinceFetch;
            fieldSetFlags()[5] = true;
            return this;
        }

        public boolean hasRetriesSinceFetch() {
            return fieldSetFlags()[5];
        }

        public com.nutch.storage.WebPage.Builder clearRetriesSinceFetch() {
            fieldSetFlags()[5] = false;
            return this;
        }


        public Long getModifiedTime() {
            return modifiedTime;
        }

        public com.nutch.storage.WebPage.Builder setModifiedTime(Long modifiedTime) {
            validate(fields()[6], modifiedTime);
            this.modifiedTime = modifiedTime;
            fieldSetFlags()[6] = true;
            return this;
        }

        public boolean hasModifiedTime() {
            return fieldSetFlags()[6];
        }

        public com.nutch.storage.WebPage.Builder clearModifiedTime() {
            fieldSetFlags()[6] = false;
            return this;
        }


        public Long getPrevModifiedTime() {
            return prevModifiedTime;
        }

        public com.nutch.storage.WebPage.Builder setPrevModifiedTime(Long prevModifiedTime) {
            validate(fields()[7], prevModifiedTime);
            this.prevModifiedTime = prevModifiedTime;
            fieldSetFlags()[7] = true;
            return this;
        }

        public boolean hasPrevModifiedTime() {
            return fieldSetFlags()[7];
        }

        public com.nutch.storage.WebPage.Builder clearPrevModifiedTime() {
            fieldSetFlags()[7] = false;
            return this;
        }


        public ProtocolStatus getProtocolStatus() {
            return protocolStatus;
        }

        public com.nutch.storage.WebPage.Builder setProtocolStatus(ProtocolStatus protocolStatus) {
            validate(fields()[8], protocolStatus);
            this.protocolStatus = protocolStatus;
            fieldSetFlags()[8] = true;
            return this;
        }

        public boolean hasProtocolStatus() {
            return fieldSetFlags()[8];
        }

        public com.nutch.storage.WebPage.Builder clearProtocolStatus() {
            protocolStatus = null;
            fieldSetFlags()[8] = false;
            return this;
        }


        public ByteBuffer getContent() {
            return content;
        }

        public com.nutch.storage.WebPage.Builder setContent(ByteBuffer content) {
            validate(fields()[9], content);
            this.content = content;
            fieldSetFlags()[9] = true;
            return this;
        }

        public boolean hasContent() {
            return fieldSetFlags()[9];
        }

        public com.nutch.storage.WebPage.Builder clearContent() {
            content = null;
            fieldSetFlags()[9] = false;
            return this;
        }


        public CharSequence getContentType() {
            return contentType;
        }

        public com.nutch.storage.WebPage.Builder setContentType(CharSequence contentType) {
            validate(fields()[10], contentType);
            this.contentType = contentType;
            fieldSetFlags()[10] = true;
            return this;
        }

        public boolean hasContentType() {
            return fieldSetFlags()[10];
        }

        public com.nutch.storage.WebPage.Builder clearContentType() {
            contentType = null;
            fieldSetFlags()[10] = false;
            return this;
        }


        public ByteBuffer getPrevSignature() {
            return prevSignature;
        }

        public com.nutch.storage.WebPage.Builder setPrevSignature(ByteBuffer prevSignature) {
            validate(fields()[11], prevSignature);
            this.prevSignature = prevSignature;
            fieldSetFlags()[11] = true;
            return this;
        }

        public boolean hasPrevSignature() {
            return fieldSetFlags()[11];
        }

        public com.nutch.storage.WebPage.Builder clearPrevSignature() {
            prevSignature = null;
            fieldSetFlags()[11] = false;
            return this;
        }


        public ByteBuffer getSignature() {
            return signature;
        }

        public com.nutch.storage.WebPage.Builder setSignature(ByteBuffer signature) {
            validate(fields()[12], signature);
            this.signature = signature;
            fieldSetFlags()[12] = true;
            return this;
        }

        public boolean hasSignature() {
            return fieldSetFlags()[12];
        }

        public com.nutch.storage.WebPage.Builder clearSignature() {
            signature = null;
            fieldSetFlags()[12] = false;
            return this;
        }


        public CharSequence getTitle() {
            return title;
        }

        public com.nutch.storage.WebPage.Builder setTitle(CharSequence title) {
            validate(fields()[13], title);
            this.title = title;
            fieldSetFlags()[13] = true;
            return this;
        }

        public boolean hasTitle() {
            return fieldSetFlags()[13];
        }

        public com.nutch.storage.WebPage.Builder clearTitle() {
            title = null;
            fieldSetFlags()[13] = false;
            return this;
        }


        public CharSequence getText() {
            return text;
        }

        public com.nutch.storage.WebPage.Builder setText(CharSequence text) {
            validate(fields()[14], text);
            this.text = text;
            fieldSetFlags()[14] = true;
            return this;
        }

        public boolean hasText() {
            return fieldSetFlags()[14];
        }

        public com.nutch.storage.WebPage.Builder clearText() {
            text = null;
            fieldSetFlags()[14] = false;
            return this;
        }


        public ParseStatus getParseStatus() {
            return parseStatus;
        }

        public com.nutch.storage.WebPage.Builder setParseStatus(ParseStatus parseStatus) {
            validate(fields()[15], parseStatus);
            this.parseStatus = parseStatus;
            fieldSetFlags()[15] = true;
            return this;
        }

        public boolean hasParseStatus() {
            return fieldSetFlags()[15];
        }

        public com.nutch.storage.WebPage.Builder clearParseStatus() {
            parseStatus = null;
            fieldSetFlags()[15] = false;
            return this;
        }


        public Float getScore() {
            return score;
        }

        public com.nutch.storage.WebPage.Builder setScore(Float score) {
            validate(fields()[16], score);
            this.score = score;
            fieldSetFlags()[16] = true;
            return this;
        }

        public boolean hasScore() {
            return fieldSetFlags()[16];
        }

        public com.nutch.storage.WebPage.Builder clearScore() {
            fieldSetFlags()[16] = false;
            return this;
        }


        public CharSequence getReprUrl() {
            return reprUrl;
        }

        public com.nutch.storage.WebPage.Builder setReprUrl(CharSequence reprUrl) {
            validate(fields()[17], reprUrl);
            this.reprUrl = reprUrl;
            fieldSetFlags()[17] = true;
            return this;
        }

        public boolean hasReprUrl() {
            return fieldSetFlags()[17];
        }

        public com.nutch.storage.WebPage.Builder clearReprUrl() {
            reprUrl = null;
            fieldSetFlags()[17] = false;
            return this;
        }


        public Map<CharSequence, CharSequence> getHeaders() {
            return headers;
        }

        public com.nutch.storage.WebPage.Builder setHeaders(Map<CharSequence, CharSequence> headers) {
            validate(fields()[18], headers);
            this.headers = headers;
            fieldSetFlags()[18] = true;
            return this;
        }

        public boolean hasHeaders() {
            return fieldSetFlags()[18];
        }

        public com.nutch.storage.WebPage.Builder clearHeaders() {
            headers = null;
            fieldSetFlags()[18] = false;
            return this;
        }


        public Map<CharSequence, CharSequence> getOutlinks() {
            return outlinks;
        }

        public com.nutch.storage.WebPage.Builder setOutlinks(Map<CharSequence, CharSequence> outlinks) {
            validate(fields()[19], outlinks);
            this.outlinks = outlinks;
            fieldSetFlags()[19] = true;
            return this;
        }

        public boolean hasOutlinks() {
            return fieldSetFlags()[19];
        }

        public com.nutch.storage.WebPage.Builder clearOutlinks() {
            outlinks = null;
            fieldSetFlags()[19] = false;
            return this;
        }


        public Map<CharSequence, CharSequence> getInlinks() {
            return inlinks;
        }

        public com.nutch.storage.WebPage.Builder setInlinks(Map<CharSequence, CharSequence> inlinks) {
            validate(fields()[20], inlinks);
            this.inlinks = inlinks;
            fieldSetFlags()[20] = true;
            return this;
        }

        public boolean hasInlinks() {
            return fieldSetFlags()[20];
        }

        public com.nutch.storage.WebPage.Builder clearInlinks() {
            inlinks = null;
            fieldSetFlags()[20] = false;
            return this;
        }


        public Map<CharSequence, CharSequence> getMarkers() {
            return markers;
        }

        public com.nutch.storage.WebPage.Builder setMarkers(Map<CharSequence, CharSequence> markers) {
            validate(fields()[21], markers);
            this.markers = markers;
            fieldSetFlags()[21] = true;
            return this;
        }

        public boolean hasMarkers() {
            return fieldSetFlags()[21];
        }

        public com.nutch.storage.WebPage.Builder clearMarkers() {
            markers = null;
            fieldSetFlags()[21] = false;
            return this;
        }


        public Map<CharSequence, ByteBuffer> getMetadata() {
            return metadata;
        }

        public com.nutch.storage.WebPage.Builder setMetadata(Map<CharSequence, ByteBuffer> metadata) {
            validate(fields()[22], metadata);
            this.metadata = metadata;
            fieldSetFlags()[22] = true;
            return this;
        }

        public boolean hasMetadata() {
            return fieldSetFlags()[22];
        }

        public com.nutch.storage.WebPage.Builder clearMetadata() {
            metadata = null;
            fieldSetFlags()[22] = false;
            return this;
        }


        public CharSequence getBatchId() {
            return batchId;
        }

        public com.nutch.storage.WebPage.Builder setBatchId(CharSequence batchId) {
            validate(fields()[23], batchId);
            this.batchId = batchId;
            fieldSetFlags()[23] = true;
            return this;
        }

        public boolean hasBatchId() {
            return fieldSetFlags()[23];
        }

        public com.nutch.storage.WebPage.Builder clearBatchId() {
            batchId = null;
            fieldSetFlags()[23] = false;
            return this;
        }

        @Override
        public WebPage build() {
            try {
                WebPage record = new WebPage();
                record.baseUrl = fieldSetFlags()[0] ? this.baseUrl : (CharSequence) defaultValue(fields()[0]);

                record.status = fieldSetFlags()[1] ? this.status : (Integer) defaultValue(fields()[1]);

                record.fetchTime = fieldSetFlags()[2] ? this.fetchTime : (Long) defaultValue(fields()[2]);

                record.prevFetchTime = fieldSetFlags()[3] ? this.prevFetchTime : (Long) defaultValue(fields()[3]);

                record.fetchInterval = fieldSetFlags()[4] ? this.fetchInterval : (Integer) defaultValue(fields()[4]);

                record.retriesSinceFetch = fieldSetFlags()[5] ? this.retriesSinceFetch : (Integer) defaultValue(fields()[5]);

                record.modifiedTime = fieldSetFlags()[6] ? this.modifiedTime : (Long) defaultValue(fields()[6]);

                record.prevModifiedTime = fieldSetFlags()[7] ? this.prevModifiedTime : (Long) defaultValue(fields()[7]);

                record.protocolStatus = fieldSetFlags()[8] ? this.protocolStatus : (ProtocolStatus) defaultValue(fields()[8]);

                record.content = fieldSetFlags()[9] ? this.content : (ByteBuffer) defaultValue(fields()[9]);

                record.contentType = fieldSetFlags()[10] ? this.contentType : (CharSequence) defaultValue(fields()[10]);

                record.prevSignature = fieldSetFlags()[11] ? this.prevSignature : (ByteBuffer) defaultValue(fields()[11]);

                record.signature = fieldSetFlags()[12] ? this.signature : (ByteBuffer) defaultValue(fields()[12]);

                record.title = fieldSetFlags()[13] ? this.title : (CharSequence) defaultValue(fields()[13]);

                record.text = fieldSetFlags()[14] ? this.text : (CharSequence) defaultValue(fields()[14]);

                record.parseStatus = fieldSetFlags()[15] ? this.parseStatus : (ParseStatus) defaultValue(fields()[15]);

                record.score = fieldSetFlags()[16] ? this.score : (Float) defaultValue(fields()[16]);

                record.reprUrl = fieldSetFlags()[17] ? this.reprUrl : (CharSequence) defaultValue(fields()[17]);
                record.headers = fieldSetFlags()[18] ? this.headers
                        : (java.util.Map<java.lang.CharSequence, java.lang.CharSequence>) new org.apache.gora.persistency.impl.DirtyMapWrapper(
                        (java.util.Map) defaultValue(fields()[18]));
                record.outlinks = fieldSetFlags()[19] ? this.outlinks
                        : (java.util.Map<java.lang.CharSequence, java.lang.CharSequence>) new org.apache.gora.persistency.impl.DirtyMapWrapper(
                        (java.util.Map) defaultValue(fields()[19]));
                record.inlinks = fieldSetFlags()[20] ? this.inlinks
                        : (java.util.Map<java.lang.CharSequence, java.lang.CharSequence>) new org.apache.gora.persistency.impl.DirtyMapWrapper(
                        (java.util.Map) defaultValue(fields()[20]));
                record.markers = fieldSetFlags()[21] ? this.markers
                        : (java.util.Map<java.lang.CharSequence, java.lang.CharSequence>) new org.apache.gora.persistency.impl.DirtyMapWrapper(
                        (java.util.Map) defaultValue(fields()[21]));
                record.metadata = fieldSetFlags()[22] ? this.metadata
                        : (java.util.Map<java.lang.CharSequence, java.nio.ByteBuffer>) new org.apache.gora.persistency.impl.DirtyMapWrapper(
                        (java.util.Map) defaultValue(fields()[22]));
                record.batchId = fieldSetFlags()[23] ? this.batchId : (CharSequence) defaultValue(fields()[23]);
                return record;
            } catch (Exception e) {
                throw new org.apache.avro.AvroRuntimeException(e);
            }
        }
    }

    public static final class Tombstone extends WebPage implements org.apache.gora.persistency.Tombstone {
        private Tombstone() {
        }

        @Override
        public CharSequence getBaseUrl() {
            throw new UnsupportedOperationException("Get is not supported on tombstones");
        }

        @Override
        public void setBaseUrl(CharSequence value) {
            throw new UnsupportedOperationException("Set is not supported on tombstones");
        }

        @Override
        public boolean isBaseUrlDirty(CharSequence value) {
            throw new UnsupportedOperationException("IsDirty is not supported on tombstones");
        }


        @Override
        public Integer getStatus() {
            throw new UnsupportedOperationException("Get is not supported on tombstones");
        }

        @Override
        public void setStatus(Integer value) {
            throw new UnsupportedOperationException("Set is not supported on tombstones");
        }

        @Override
        public boolean isStatusDirty(Integer value) {
            throw new UnsupportedOperationException("IsDirty is not supported on tombstones");
        }


        @Override
        public Long getFetchTime() {
            throw new UnsupportedOperationException("Get is not supported on tombstones");
        }

        @Override
        public void setFetchTime(Long value) {
            throw new UnsupportedOperationException("Set is not supported on tombstones");
        }

        @Override
        public boolean isFetchTimeDirty(Long value) {
            throw new UnsupportedOperationException("IsDirty is not supported on tombstones");
        }


        @Override
        public Long getPrevFetchTime() {
            throw new UnsupportedOperationException("Get is not supported on tombstones");
        }

        @Override
        public void setPrevFetchTime(Long value) {
            throw new UnsupportedOperationException("Set is not supported on tombstones");
        }

        @Override
        public boolean isPrevFetchTimeDirty(Long value) {
            throw new UnsupportedOperationException("IsDirty is not supported on tombstones");
        }


        @Override
        public Integer getFetchInterval() {
            throw new UnsupportedOperationException("Get is not supported on tombstones");
        }

        @Override
        public void setFetchInterval(Integer value) {
            throw new UnsupportedOperationException("Set is not supported on tombstones");
        }

        @Override
        public boolean isFetchIntervalDirty(Integer value) {
            throw new UnsupportedOperationException("IsDirty is not supported on tombstones");
        }


        @Override
        public Integer getRetriesSinceFetch() {
            throw new UnsupportedOperationException("Get is not supported on tombstones");
        }

        @Override
        public void setRetriesSinceFetch(Integer value) {
            throw new UnsupportedOperationException("Set is not supported on tombstones");
        }

        @Override
        public boolean isRetriesSinceFetchDirty(Integer value) {
            throw new UnsupportedOperationException("IsDirty is not supported on tombstones");
        }


        @Override
        public Long getModifiedTime() {
            throw new UnsupportedOperationException("Get is not supported on tombstones");
        }

        @Override
        public void setModifiedTime(Long value) {
            throw new UnsupportedOperationException("Set is not supported on tombstones");
        }

        @Override
        public boolean isModifiedTimeDirty(Long value) {
            throw new UnsupportedOperationException("IsDirty is not supported on tombstones");
        }


        @Override
        public Long getPrevModifiedTime() {
            throw new UnsupportedOperationException("Get is not supported on tombstones");
        }

        @Override
        public void setPrevModifiedTime(Long value) {
            throw new UnsupportedOperationException("Set is not supported on tombstones");
        }

        @Override
        public boolean isPrevModifiedTimeDirty(Long value) {
            throw new UnsupportedOperationException("IsDirty is not supported on tombstones");
        }


        @Override
        public ProtocolStatus getProtocolStatus() {
            throw new UnsupportedOperationException("Get is not supported on tombstones");
        }

        @Override
        public void setProtocolStatus(ProtocolStatus value) {
            throw new UnsupportedOperationException("Set is not supported on tombstones");
        }

        @Override
        public boolean isProtocolStatusDirty(ProtocolStatus value) {
            throw new UnsupportedOperationException("IsDirty is not supported on tombstones");
        }


        @Override
        public ByteBuffer getContent() {
            throw new UnsupportedOperationException("Get is not supported on tombstones");
        }

        @Override
        public void setContent(ByteBuffer value) {
            throw new UnsupportedOperationException("Set is not supported on tombstones");
        }

        @Override
        public boolean isContentDirty(ByteBuffer value) {
            throw new UnsupportedOperationException("IsDirty is not supported on tombstones");
        }


        @Override
        public CharSequence getContentType() {
            throw new UnsupportedOperationException("Get is not supported on tombstones");
        }

        @Override
        public void setContentType(CharSequence value) {
            throw new UnsupportedOperationException("Set is not supported on tombstones");
        }

        @Override
        public boolean isContentTypeDirty(CharSequence value) {
            throw new UnsupportedOperationException("IsDirty is not supported on tombstones");
        }


        @Override
        public ByteBuffer getPrevSignature() {
            throw new UnsupportedOperationException("Get is not supported on tombstones");
        }

        @Override
        public void setPrevSignature(ByteBuffer value) {
            throw new UnsupportedOperationException("Set is not supported on tombstones");
        }

        @Override
        public boolean isPrevSignatureDirty(ByteBuffer value) {
            throw new UnsupportedOperationException("IsDirty is not supported on tombstones");
        }


        @Override
        public ByteBuffer getSignature() {
            throw new UnsupportedOperationException("Get is not supported on tombstones");
        }

        @Override
        public void setSignature(ByteBuffer value) {
            throw new UnsupportedOperationException("Set is not supported on tombstones");
        }

        @Override
        public boolean isSignatureDirty(ByteBuffer value) {
            throw new UnsupportedOperationException("IsDirty is not supported on tombstones");
        }


        @Override
        public CharSequence getTitle() {
            throw new UnsupportedOperationException("Get is not supported on tombstones");
        }

        @Override
        public void setTitle(CharSequence value) {
            throw new UnsupportedOperationException("Set is not supported on tombstones");
        }

        @Override
        public boolean isTitleDirty(CharSequence value) {
            throw new UnsupportedOperationException("IsDirty is not supported on tombstones");
        }


        @Override
        public CharSequence getText() {
            throw new UnsupportedOperationException("Get is not supported on tombstones");
        }

        @Override
        public void setText(CharSequence value) {
            throw new UnsupportedOperationException("Set is not supported on tombstones");
        }

        @Override
        public boolean isTextDirty(CharSequence value) {
            throw new UnsupportedOperationException("IsDirty is not supported on tombstones");
        }


        @Override
        public ParseStatus getParseStatus() {
            throw new UnsupportedOperationException("Get is not supported on tombstones");
        }

        @Override
        public void setParseStatus(ParseStatus value) {
            throw new UnsupportedOperationException("Set is not supported on tombstones");
        }

        @Override
        public boolean isParseStatusDirty(ParseStatus value) {
            throw new UnsupportedOperationException("IsDirty is not supported on tombstones");
        }


        @Override
        public Float getScore() {
            throw new UnsupportedOperationException("Get is not supported on tombstones");
        }

        @Override
        public void setScore(Float value) {
            throw new UnsupportedOperationException("Set is not supported on tombstones");
        }

        @Override
        public boolean isScoreDirty(Float value) {
            throw new UnsupportedOperationException("IsDirty is not supported on tombstones");
        }


        @Override
        public CharSequence getReprUrl() {
            throw new UnsupportedOperationException("Get is not supported on tombstones");
        }

        @Override
        public void setReprUrl(CharSequence value) {
            throw new UnsupportedOperationException("Set is not supported on tombstones");
        }

        @Override
        public boolean isReprUrlDirty(CharSequence value) {
            throw new UnsupportedOperationException("IsDirty is not supported on tombstones");
        }


        @Override
        public Map<CharSequence, CharSequence> getHeaders() {
            throw new UnsupportedOperationException("Get is not supported on tombstones");
        }

        @Override
        public void setHeaders(Map<CharSequence, CharSequence> value) {
            throw new UnsupportedOperationException("Set is not supported on tombstones");
        }

        @Override
        public boolean isHeadersDirty(Map<CharSequence, CharSequence> value) {
            throw new UnsupportedOperationException("IsDirty is not supported on tombstones");
        }


        @Override
        public Map<CharSequence, CharSequence> getOutlinks() {
            throw new UnsupportedOperationException("Get is not supported on tombstones");
        }

        @Override
        public void setOutlinks(Map<CharSequence, CharSequence> value) {
            throw new UnsupportedOperationException("Set is not supported on tombstones");
        }

        @Override
        public boolean isOutlinksDirty(Map<CharSequence, CharSequence> value) {
            throw new UnsupportedOperationException("IsDirty is not supported on tombstones");
        }


        @Override
        public Map<CharSequence, CharSequence> getInlinks() {
            throw new UnsupportedOperationException("Get is not supported on tombstones");
        }

        @Override
        public void setInlinks(Map<CharSequence, CharSequence> value) {
            throw new UnsupportedOperationException("Set is not supported on tombstones");
        }

        @Override
        public boolean isInlinksDirty(Map<CharSequence, CharSequence> value) {
            throw new UnsupportedOperationException("IsDirty is not supported on tombstones");
        }


        @Override
        public Map<CharSequence, CharSequence> getMarkers() {
            throw new UnsupportedOperationException("Get is not supported on tombstones");
        }

        @Override
        public void setMarkers(Map<CharSequence, CharSequence> value) {
            throw new UnsupportedOperationException("Set is not supported on tombstones");
        }

        @Override
        public boolean isMarkersDirty(Map<CharSequence, CharSequence> value) {
            throw new UnsupportedOperationException("IsDirty is not supported on tombstones");
        }


        @Override
        public Map<CharSequence, ByteBuffer> getMetadata() {
            throw new UnsupportedOperationException("Get is not supported on tombstones");
        }

        @Override
        public void setMetadata(Map<CharSequence, ByteBuffer> value) {
            throw new UnsupportedOperationException("Set is not supported on tombstones");
        }

        @Override
        public boolean isMetadataDirty(Map<CharSequence, ByteBuffer> value) {
            throw new UnsupportedOperationException("IsDirty is not supported on tombstones");
        }


        @Override
        public CharSequence getBatchId() {
            throw new UnsupportedOperationException("Get is not supported on tombstones");
        }

        @Override
        public void setBatchId(CharSequence value) {
            throw new UnsupportedOperationException("Set is not supported on tombstones");
        }

        @Override
        public boolean isBatchIdDirty(CharSequence value) {
            throw new UnsupportedOperationException("IsDirty is not supported on tombstones");
        }
    }

    private static final Tombstone TOMBSTONE = new Tombstone();

}

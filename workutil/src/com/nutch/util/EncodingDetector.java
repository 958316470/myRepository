package com.nutch.util;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import com.nutch.net.protocols.Response;
import com.nutch.storage.WebPage;
import org.apache.avro.util.Utf8;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class EncodingDetector {

    public final static Utf8 CONTENT_TYPE_UTF8 = new Utf8(Response.CONTENT_TYPE);

    private class EncodingClue {
        private final String value;
        private final String source;
        private final int confidence;

        public EncodingClue(String value, String source) {
            this(value, source, NO_THRESHOLD);
        }

        public EncodingClue(String value, String source, int confidence) {
            this.value = value.toLowerCase();
            this.source = source;
            this.confidence = confidence;
        }

        public String getSource() {
            return source;
        }

        public String getValue() {return value;}

        @Override
        public String toString() {
          return value + " (" + source + ((confidence > 0) ? ", " + confidence + "% confidence" : "") + ")";
        }

        public boolean isEmpty() {
            return (value == null || "".equals(value));
        }
        public boolean meetsThresHold() {
            return (confidence < 0 || (minConfidence >= 0 && confidence >= minConfidence));
        }
    }
    public static final Logger LOG = LoggerFactory.getLogger(EncodingDetector.class);
    public static final int NO_THRESHOLD = -1;
    public static final String MIN_CONFIDENCE_KEY = "encodingdetector.charset.min.confidence";
    private static final HashMap<String,String> ALIASES = new HashMap<String, String>();
    private static final HashSet<String> DETECTABLES = new HashSet<String>();
    private static final int MIN_LENGTH = 4;

    static {
        DETECTABLES.add("text/html");
        DETECTABLES.add("text/plain");
        DETECTABLES.add("text/richtext");
        DETECTABLES.add("text/rtf");
        DETECTABLES.add("text/sgml");
        DETECTABLES.add("text/tab-separated-value");
        DETECTABLES.add("text/xml");
        DETECTABLES.add("application/rss+xml");
        DETECTABLES.add("application/xhtml+xml");

        ALIASES.put("ISO-8859-1","windows-1252");
        ALIASES.put("EUC-KR","x-windows-949");
        ALIASES.put("x-EUC-CN","GB18030");
        ALIASES.put("GBK","GB18030");
        // ALIASES.put("Big5", "Big5HKSCS");
        // ALIASES.put("TIS620", "Cp874");
        // ALIASES.put("ISO-8859-11", "Cp874");
    }

    private final int minConfidence;

    private final CharsetDetector detector;

    private final List<EncodingClue> clues;

    public EncodingDetector(Configuration conf) {
        minConfidence = conf.getInt(MIN_CONFIDENCE_KEY,-1);
        detector = new CharsetDetector();
        clues = new ArrayList<EncodingClue>();
    }

    public void autoDetectClues(WebPage page, boolean filter) {
        autoDetectClues(page.getContent(), page.getContentType(),
                parseCharacterEncoding(page.getHeaders().get(CONTENT_TYPE_UTF8)),filter);
    }

    private void autoDetectClues(ByteBuffer dataBuffer, CharSequence typeUtf8, String encoding, boolean filter) {
        int length = dataBuffer.remaining();
        String type = TableUtil.toString(typeUtf8);
        if (minConfidence >= 0 && DETECTABLES.contains(type) && length > MIN_LENGTH) {
            CharsetMatch[] matches = null;
            try {
                detector.enableInputFilter(filter);
                detector.setText(new ByteArrayInputStream(dataBuffer.array(),dataBuffer.arrayOffset() + dataBuffer.position(),length));
                matches = detector.detectAll();
            } catch (Exception e) {
                LOG.debug("Exception from ICU4J (ignoring): ", e);
            }

            if (matches != null) {
                for (CharsetMatch match : matches) {
                    addClue(match.getName(), "detect", match.getConfidence());
                }
            }
        }
        addClue(encoding, "header");
    }

    public void addClue(String value, String source, int confidence) {
        if (value == null || "".equals(value)) {
            return;
        }
        value = resolveEncodingAlias(value);
        if (value != null) {
            clues.add(new EncodingClue(value, source, confidence));
        }
    }

    public void addClue(String value, String source) {
        addClue(value, source, NO_THRESHOLD);
    }

    public String guessEncoding(WebPage page, String defaultValue) {
        CharSequence baseUrlUtf8 = page.getBaseUrl();
        String baseUrl = TableUtil.toString(baseUrlUtf8);
        return guessEncoding(baseUrl, defaultValue);
    }

    private String guessEncoding(String baseUrl, String defaultValue) {
        if (LOG.isTraceEnabled()) {
            findDisagreements(baseUrl, clues);
        }
        EncodingClue defaultClue = new EncodingClue(defaultValue, "default");
        EncodingClue bestClue = defaultClue;

        for (EncodingClue clue : clues) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(baseUrl + ": charset " + clue);
            }
            String charset = clue.value;
            if (minConfidence >= 0 && clue.confidence >= minConfidence) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace(baseUrl + ": Choosing encoding: " + charset + " with confidence " + clue.confidence);
                }
                return resolveEncodingAlias(charset).toLowerCase();
            } else if (clue.confidence == NO_THRESHOLD && bestClue == defaultClue) {
                bestClue = clue;
            }
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace(baseUrl + ": Choosing encoding: " + bestClue);
        }
        return bestClue.value.toLowerCase();
    }

    public void clearCLues(){
        clues.clear();
    }

    private void findDisagreements(String url, List<EncodingClue> newClues) {
        HashSet<String> valsSeen = new HashSet<String>();
        HashSet<String> sourcesSeen = new HashSet<String>();
        boolean disagreement = false;
        for (int i = 0; i < newClues.size(); i++) {
            EncodingClue clue = newClues.get(i);
            if (!clue.isEmpty() && !sourcesSeen.contains(clue.source)) {
                if (valsSeen.size() > 0 && !valsSeen.contains(clue.value) && clue.meetsThresHold()) {
                    disagreement = true;
                }
                if (clue.meetsThresHold()) {
                    valsSeen.add(clue.value);
                }
                sourcesSeen.add(clue.source);
            }
        }
        if (disagreement) {
            StringBuffer sb = new StringBuffer();
            sb.append("Disagreement: " + url + ";");
            for (int i = 0; i < newClues.size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(newClues.get(i));
            }
            LOG.trace(sb.toString());
        }
    }

    public static String resolveEncodingAlias(String encoding) {
        try {
            if (encoding == null || !Charset.isSupported(encoding)) {
                return null;
            }
            String canonicalName = new String(Charset.forName(encoding).name());
            return ALIASES.containsKey(canonicalName) ? ALIASES.get(canonicalName) : canonicalName;
        } catch (Exception e) {
            LOG.warn("Invalid encoding " + encoding + " detected, using default.");
            return null;
        }
    }
    public static String parseCharacterEncoding(CharSequence contentTypeUtf8) {
        if (contentTypeUtf8 == null) {
            return null;
        }
        String contentType = contentTypeUtf8.toString();
        int start = contentType.indexOf("charset=");
        if (start < 0) {
            return null;
        }
        String encoding = contentType.substring(start + 8);
        int end = encoding.indexOf(";");
        if (end >= 0) {
            encoding = encoding.substring(0,end);
        }
        encoding = encoding.trim();
        if ((encoding.length() > 2) && (encoding.startsWith("\"")) && (encoding.endsWith("\""))) {
            encoding = encoding.substring(1, encoding.length() - 1);
        }
        return encoding.trim();
    }
}

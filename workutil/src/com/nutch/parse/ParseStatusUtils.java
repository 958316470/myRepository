package com.nutch.parse;

import com.nutch.storage.ParseStatus;
import com.nutch.util.TableUtil;
import org.apache.avro.util.Utf8;
import org.apache.hadoop.conf.Configuration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ParseStatusUtils {

    public static ParseStatus STATUS_SUCCESS = ParseStatus.newBuilder().build();
    public static final HashMap<Short, String> minorCodes = new HashMap<Short, String>();

    static {
        STATUS_SUCCESS.setMajorCode((int) ParseStatusCodes.SUCCESS);
        minorCodes.put(ParseStatusCodes.SUCCESS_OK, "ok");
        minorCodes.put(ParseStatusCodes.SUCCESS_REDIRECT, "redirect");
        minorCodes.put(ParseStatusCodes.FAILED_EXCEPTION, "exception");
        minorCodes.put(ParseStatusCodes.FAILED_INVALID_FORMAT, "invalid_format");
        minorCodes.put(ParseStatusCodes.FAILED_MISSING_CONTENT, "missing_content");
        minorCodes.put(ParseStatusCodes.FAILED_MISSING_PARTS, "missing_parts");
        minorCodes.put(ParseStatusCodes.FAILED_TRUNCATED, "truncated");
    }

    public static boolean isSuccess(ParseStatus status) {
        if (status == null) {
            return false;
        }
        return status.getMajorCode() == ParseStatusCodes.SUCCESS;
    }

    public static String getMessage(ParseStatus status) {
        List<CharSequence> args = status.getArgs();
        if (args != null && args.size() > 0) {
            return TableUtil.toString(args.iterator().next());
        }
        return null;
    }

    public static String getArg(ParseStatus status, int n) {
        List<CharSequence> args = status.getArgs();
        if (args == null) {
            return null;
        }
        int i = 0;
        for (CharSequence arg : args) {
            if (i == n) {
                return TableUtil.toString(arg);
            }
            i++;
        }
        return null;
    }

    public static Parse getEmptyParse(Exception e, Configuration conf) {
        ParseStatus status = ParseStatus.newBuilder().build();
        status.setMajorCode((int)ParseStatusCodes.FAILED);
        status.setMinorCode((int) ParseStatusCodes.FAILED_EXCEPTION);
        status.getArgs().add(new Utf8(e.toString()));
        return new Parse("","", new Outlink[0], status);
    }

    public static Parse getEmptyParse(int minorCode, String message, Configuration conf) {
        ParseStatus status = ParseStatus.newBuilder().build();
        status.setMajorCode((int) ParseStatusCodes.FAILED);
        status.setMinorCode(minorCode);
        status.getArgs().add(new Utf8(message));
        return new Parse("", "", new Outlink[0], status);
    }

    public static String toString(ParseStatus status) {
        if (status == null) {
            return "(null)";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(ParseStatusCodes.majorCodes[status.getMajorCode()] + "/" + minorCodes.get(status.getMinorCode().shortValue()));
        sb.append(" (" + status.getMajorCode() + "/" + status.getMinorCode() + ")");
        sb.append(", args=[");
        List<CharSequence> args = status.getArgs();
        if (args != null) {
            int i = 0;
            Iterator<CharSequence> it = args.iterator();
            while (it.hasNext()) {
                if (i > 0) {
                    sb.append(',');
                }
                sb.append(it.next());
                i++;
            }
        }
        sb.append("]");
        return sb.toString();
    }
}

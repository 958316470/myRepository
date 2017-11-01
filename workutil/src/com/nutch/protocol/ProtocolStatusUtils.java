package com.nutch.protocol;

import com.nutch.storage.ProtocolStatus;
import com.nutch.util.TableUtil;
import org.apache.avro.util.Utf8;

import java.net.URL;
import java.util.Iterator;
import java.util.List;

public class ProtocolStatusUtils implements ProtocolStatusCodes{

    public static final ProtocolStatus STATUS_SUCCESS = makeStatus(SUCCESS);
    public static final ProtocolStatus STATUS_FAILED = makeStatus(FAILED);
    public static final ProtocolStatus STATUS_PROTO_NOT_FOUND = makeStatus(PROTO_NOT_FOUND);
    public static final ProtocolStatus STATUS_GONE = makeStatus(GONE);
    public static final ProtocolStatus STATUS_MOVED = makeStatus(MOVED);
    public static final ProtocolStatus STATUS_TEMP_MOVED = makeStatus(TEMP_MOVED);
    public static final ProtocolStatus STATUS_NOTFOUND = makeStatus(NOTFOUND);
    public static final ProtocolStatus STATUS_RETRY = makeStatus(RETRY);
    public static final ProtocolStatus STATUS_EXCEPTION = makeStatus(EXCEPTION);
    public static final ProtocolStatus STATUS_ACCESS_DENIED = makeStatus(ACCESS_DENIED);
    public static final ProtocolStatus STATUS_ROBOTS_DENIED = makeStatus(ROBOTS_DENIED);
    public static final ProtocolStatus STATUS_REDIR_EXCEEDED = makeStatus(REDIR_EXCEEDED);
    public static final ProtocolStatus STATUS_NOTFETCHING = makeStatus(NOTFETCHING);
    public static final ProtocolStatus STATUS_NOTMODIFIED = makeStatus(NOTMODIFIED);
    public static final ProtocolStatus STATUS_WOULDBLOCK = makeStatus(WOULDBLOCK);
    public static final ProtocolStatus STATUS_BLOCKED = makeStatus(BLOCKED);

    public static String getName(int code) {
        if(code == SUCCESS) {
            return "SUCCESS";
        }else if (code == FAILED) {
            return "FAILED";
        }else if (code == PROTO_NOT_FOUND) {
            return "PROTO_NOT_FOUND";
        }else if (code == GONE) {
            return "GONE";
        }else if (code == MOVED) {
            return "MOVED";
        }else if (code == TEMP_MOVED) {
            return "TEMP_MOVED";
        }else if (code == NOTFOUND) {
            return "NOTFOUND";
        }else if (code == RETRY) {
            return "RETRY";
        }else if (code == EXCEPTION) {
            return "EXCEPTION";
        }else if (code == ACCESS_DENIED) {
            return "ACCESS_DENIED";
        }else if (code == ROBOTS_DENIED) {
            return "ROBOTS_DENIED";
        }else if (code == REDIR_EXCEEDED) {
            return "REDIR_EXCEEDED";
        }else if (code == NOTFETCHING) {
            return "NOTFETCHING";
        }else if (code == NOTMODIFIED) {
            return "NOTMODIFIED";
        }else if (code == WOULDBLOCK) {
            return "WOULDBLOCK";
        }else if (code == BLOCKED) {
            return "BLOCKED";
        }
        return "UNKNOWN_CODE_" + code;
    }

    public static ProtocolStatus makeStatus(int code) {
        ProtocolStatus status = ProtocolStatus.newBuilder().build();
        status.setCode(code);
        status.setLastModified(0L);
        return status;
    }

    public static ProtocolStatus makeStatus(int code, String message) {
        ProtocolStatus status = makeStatus(code);
        status.getArgs().add(new Utf8(message));
        return status;
    }

    public  static ProtocolStatus makeStatus(int code, URL url) {
        return makeStatus(code,url.toString());
    }

    public static String getMessage(ProtocolStatus status) {
        List<CharSequence> args = status.getArgs();
        if (args == null || args.size() == 0) {
            return null;
        }
        return TableUtil.toString(args.iterator().next());
    }

    public static String toString(ProtocolStatus status){
        if(status == null) {
            return "(null)";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(getName(status.getCode()));
        sb.append(", args=[");
        List<CharSequence> args = status.getArgs();
        if (args != null) {
            int i = 0;
            Iterator<CharSequence> it = args.iterator();
            while(it.hasNext()) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(it.next());
                i++;
            }
        }
        sb.append("]");
        return sb.toString();
    }
}

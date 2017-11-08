package com.nutch.net.protocols;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class HttpDateFormat {
    protected static SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);

    static {
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public static String toString(Date date) {
        String string;
        synchronized (format) {
            string = format.format(date);
        }
        return string;
    }

    public static String toString(Calendar cal) {
        String string;
        synchronized (format) {
            string = format.format(cal.getTime());
        }
        return string;
    }

    public static String toString(long time) {
       String string;
       synchronized (format) {
           string = format.format(new Date(time));
       }
       return string;
    }

    public static long toLong(String dataString) throws ParseException {
        long time;
        synchronized (format) {
            time = format.parse(dataString).getTime();
        }
        return time;
    }

    public static void main(String[] args) throws Exception {
        Date now = new Date(System.currentTimeMillis());
        String string = HttpDateFormat.toString(now);
        long time = HttpDateFormat.toLong(string);
        System.out.println(string);
        System.out.println(HttpDateFormat.toString(time));
    }
}

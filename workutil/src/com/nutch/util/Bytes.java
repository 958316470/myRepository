package com.nutch.util;

import org.apache.hadoop.io.RawComparator;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.io.WritableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.Iterator;

public class Bytes {

    private static final Logger LOG = LoggerFactory.getLogger(Bytes.class);

    public static final String UTF8_ENCODING = "UTF-8";

    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    public static final int SIZEOF_BOOLEAN = Byte.SIZE / Byte.SIZE;

    public static final int SIZEOF_BYTE = SIZEOF_BOOLEAN;

    public static final int SIZEOF_CHAR = Character.SIZE / Byte.SIZE;

    public static final int SIZEOF_DOUBLE = Double.SIZE / Byte.SIZE;

    public static final int SIZEOF_FLOAT = Float.SIZE / Byte.SIZE;

    public static final int SIZEOF_INT = Integer.SIZE / Byte.SIZE;

    public static final int SIZEOF_LONG = Long.SIZE / Byte.SIZE;

    public static final int SIZEOF_SHORT = Short.SIZE / Byte.SIZE;

    public static final int ESTIMATED_HEAP_TAX = 16;

    public static class ByteArrayComparator implements RawComparator<byte[]> {

        public ByteArrayComparator() {
            super();
        }


        @Override
        public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {
            return compareTo(b1, s1, l1, b2, s2, l2);
        }

        @Override
        public int compare(byte[] left, byte[] right) {
            return compareTo(left, right);
        }
    }

    public static Comparator<byte[]> BYTES_COMPARATOR = new ByteArrayComparator();

    public static RawComparator<byte[]> BYTES_RAWCOMPARATOR = new ByteArrayComparator();

    public static byte[] readByteArray(final DataInput in) throws IOException {
        int len = WritableUtils.readVInt(in);
        if (len < 0) {
            throw new NegativeArraySizeException(Integer.toString(len));
        }
        byte[] result = new byte[len];
        in.readFully(result, 0, len);
        return result;
    }

    public static byte[] readByteArrayThrowsRuntime(final DataInput in) {
        try {
            return readByteArray(in);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeByteArray(final DataOutput out, final byte[] b) throws IOException {
        if (b == null) {
            WritableUtils.writeVInt(out, 0);
        } else {
            writeByteArray(out, b, 0, b.length);
        }
    }

    public static void writeByteArray(final DataOutput out, final byte[] b, final int offset, final int length) throws IOException {
        WritableUtils.writeVInt(out, length);
        out.write(b, offset, length);
    }

    public static int writeByteArray(final byte[] tgt, final int tgtOffset, final byte[] src, final int srcOffset, final int srcLength) {
        byte[] vint = vintToBytes(srcLength);
        System.arraycopy(vint, 0, tgt, tgtOffset, vint.length);
        int offset = tgtOffset + vint.length;
        System.arraycopy(src, srcOffset, tgt, offset, srcLength);
        return offset + srcLength;
    }

    public static int putBytes(byte[] tgtBytes, int tgtOffset, byte[] srcBytes, int srcOffset, int srcLength) {
        System.arraycopy(srcBytes, srcOffset, tgtBytes, tgtOffset, srcLength);
        return tgtOffset + srcLength;
    }

    public static int putByte(byte[] bytes, int offset, byte b) {
        bytes[offset] = b;
        return offset + 1;
    }

    public static byte[] toBytes(ByteBuffer bb) {
        int length = bb.remaining();
        byte[] result = new byte[length];
        System.arraycopy(bb.array(), bb.arrayOffset() + bb.position(), result, 0, length);
        return result;
    }

    public static String toString(ByteBuffer bb) {
        return bb == null ? null : toString(bb.array(), bb.arrayOffset() + bb.position(), bb.remaining());
    }

    public static String toString(final byte[] b) {
        return b == null ? null : toString(b, 0, b.length);
    }

    public static String toString(final byte[] b1, String sep, final byte[] b2) {
        return toString(b1, 0, b1.length) + sep + toString(b2, 0, b2.length);
    }

    public static String toString(final byte[] b, int off, int len) {
        if (b == null) {
            return null;
        }
        if (len == 0) {
            return "";
        }
        try {
            return new String(b, off, len, UTF8_ENCODING);
        } catch (UnsupportedEncodingException e) {
            LOG.error("UTF-8 not supported?", e);
            return null;
        }
    }

    public static String toStringBinary(ByteBuffer bb) {
        return bb == null ? null : toStringBinary(bb.array(), bb.arrayOffset() + bb.position(), bb.remaining());
    }

    public static String toStringBinary(final byte[] b) {
        return toStringBinary(b, 0, b.length);
    }

    public static String toStringBinary(final byte[] b, int off, int len) {
        StringBuilder result = new StringBuilder();
        try {
            String first = new String(b, off, len, "ISO-8859-1");
            for (int i = 0; i < first.length(); i++) {
                int ch = first.charAt(i) & 0xFF;
                boolean flag = (ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || " `~!@#$%^&*()_-+=[]\\|;:'\",.<>/?".indexOf(ch) >= 0;
                if (flag) {
                    result.append(first.charAt(i));
                } else {
                    result.append(String.format("\\x%02X", ch));
                }
            }
        } catch (UnsupportedEncodingException e) {
            LOG.error("ISO-8859-1 not supported?", e);
        }
        return result.toString();
    }

    private static boolean isHexDigit(char c) {
        return (c >= 'A' && c <= 'F') || (c >= '0' && c <= '9');
    }

    public static byte toBinaryFromHex(byte ch) {
        if (ch >= 'A' && ch <= 'F') {
            return (byte) ((byte) 10 + (byte) (ch - 'A'));
        } else {
            return (byte) (ch - '0');
        }
    }

    public static byte[] toBytesBinary(String in) {
        byte[] b = new byte[in.length()];
        int size = 0;
        for (int i = 0; i < in.length(); i++) {
            char ch = in.charAt(i);
            if (ch == '\\') {
                char next = in.charAt(i + 1);
                if (next != 'x') {
                    b[size++] = (byte) ch;
                    continue;
                }
                char hd1 = in.charAt(i + 2);
                char hd2 = in.charAt(i + 3);
                if (!isHexDigit(hd1) || !isHexDigit(hd2)) {
                    continue;
                }
                byte d = (byte) ((toBinaryFromHex((byte) hd1) << 4) + toBinaryFromHex((byte) hd2));
                b[size++] = d;
                i += 3;
            } else {
                b[size++] = (byte) ch;
            }
        }
        byte[] b2 = new byte[size];
        System.arraycopy(b, 0, b2, 0, size);
        return b2;
    }

    public static byte[] toBytes(String s) {
        try {
            return s.getBytes(UTF8_ENCODING);
        } catch (UnsupportedEncodingException e) {
            LOG.error("UTF-8 not supported?", e);
            return null;
        }
    }

    public static byte[] toBytes(final boolean b) {
        return new byte[]{b ? (byte) -1 : (byte) 0};
    }

    public static boolean toBoolean(final byte[] b) {
        if (b.length != 1) {
            throw new IllegalArgumentException("Array has wrong size: " + b.length);
        }
        return b[0] != (byte) 0;
    }

    public static byte[] toBytes(long val) {
        byte[] b = new byte[8];
        for (int i = 7; i > 0; i--) {
            b[i] = (byte) val;
            val >>>= 8;
        }
        b[0] = (byte) val;
        return b;
    }

    public static long toLong(byte[] bytes) {
        return toLong(bytes, 0, SIZEOF_LONG);
    }

    public static long toLong(byte[] bytes, int offset) {
        return toLong(bytes, offset, SIZEOF_LONG);
    }

    public static long toLong(byte[] bytes, int offset, final int length) {
        if (length != SIZEOF_LONG || offset + length > bytes.length) {
            throw explainWrongLengthOrOffset(bytes, offset, length, SIZEOF_LONG);
        }
        long l = 0;
        for (int i = offset; i < offset + length; i++) {
            l <<= 8;
            l ^= bytes[i] & 0xFF;
        }
        return l;
    }

    private static IllegalArgumentException explainWrongLengthOrOffset(final byte[] bytes, final int offset, final int length, final int expectedLength) {
        String reason;
        if (length != expectedLength) {
            reason = "Wrong length: " + length + ", expected " + expectedLength;
        } else {
            reason = "offset (" + offset + ") + length (" + length + ") exceed the capacity of the array: " + bytes.length;
        }
        return new IllegalArgumentException(reason);
    }

    public static int putLong(byte[] bytes, int offset, long val) {
        if (bytes.length - offset < SIZEOF_LONG) {
            throw new IllegalArgumentException("Not enough room to put a long at offset " + offset + " in a " + bytes.length + "byte array");
        }
        for (int i = offset + 7; i > offset; i--) {
            bytes[i] = (byte) val;
            val >>>= 8;
        }
        bytes[offset] = (byte) val;
        return offset + SIZEOF_LONG;
    }

    public static float toFloat(byte[] bytes) {
        return toFloat(bytes, 0);
    }

    public static float toFloat(byte[] bytes, int offset) {
        return Float.intBitsToFloat(toInt(bytes, offset, SIZEOF_INT));
    }

    public static int putFloat(byte[] bytes, int offset, float f) {
        return putInt(bytes, offset, Float.floatToRawIntBits(f));
    }

    public static byte[] toBytes(final float f) {
        return Bytes.toBytes(Float.floatToRawIntBits(f));
    }

    public static double toDouble(final byte[] bytes) {
        return toDouble(bytes, 0);
    }

    public static double toDouble(final byte[] bytes, final int offset) {
        return Double.longBitsToDouble(toLong(bytes, offset, SIZEOF_LONG));
    }

    public static int putDouble(byte[] bytes, int offset, double d) {
        return putLong(bytes, offset, Double.doubleToLongBits(d));
    }

    public static byte[] toBytes(final double d) {
        return Bytes.toBytes(Double.doubleToRawLongBits(d));
    }

    public static byte[] toBytes(int val) {
        byte[] b = new byte[4];
        for (int i = 3; i > 0; i--) {
            b[i] = (byte) val;
            val >>>= 8;
        }
        b[0] = (byte) val;
        return b;
    }

    public static int toInt(byte[] bytes) {
        return toInt(bytes, 0, SIZEOF_INT);
    }

    public static int toInt(byte[] bytes, int offset) {
        return toInt(bytes, offset, SIZEOF_INT);
    }

    public static int toInt(byte[] bytes, int offset, final int length) {
        if (length != SIZEOF_INT || offset + length > bytes.length) {
            throw explainWrongLengthOrOffset(bytes, offset, length, SIZEOF_INT);
        }
        int n = 0;
        for (int i = offset; i < (offset + length); i++) {
            n <<= 8;
            n ^= bytes[i] & 0xFF;
        }
        return n;
    }

    public static int putInt(byte[] bytes, int offset, int val) {
        if (bytes.length - offset < SIZEOF_INT) {
            throw new IllegalArgumentException("Not enough room to put an int at offset " + offset + " in a " + bytes.length + " byte array");
        }
        for (int i = offset + 3; i > offset; i--) {
            bytes[i] = (byte) val;
            val >>>= 8;
        }
        bytes[offset] = (byte) val;
        return offset + SIZEOF_INT;
    }

    public static byte[] toBytes(short val) {
        byte[] b = new byte[SIZEOF_SHORT];
        b[1] = (byte) val;
        val >>= 8;
        b[0] = (byte) val;
        return b;
    }

    public static short toShort(byte[] bytes) {
        return toShort(bytes, 0, SIZEOF_SHORT);
    }

    public static short toShort(byte[] bytes, int offset) {
        return toShort(bytes, offset, SIZEOF_SHORT);
    }

    public static short toShort(byte[] bytes, int offset, final int length) {
        if (length != SIZEOF_SHORT || offset + length > bytes.length) {
            throw explainWrongLengthOrOffset(bytes, offset, length, SIZEOF_SHORT);
        }
        short n = 0;
        n ^= bytes[offset] & 0xFF;
        n <<= 8;
        n ^= bytes[offset + 1] & 0xFF;
        return n;
    }

    public static int putShort(byte[] bytes, int offset, short val) {
        if (bytes.length - offset < SIZEOF_SHORT) {
            throw new IllegalArgumentException("Not enough room to put a short at offset " + offset + " in a " + bytes.length + " byte array");
        }
        bytes[offset + 1] = (byte) val;
        val >>= 8;
        bytes[offset] = (byte) val;
        return offset + SIZEOF_SHORT;
    }

    public static byte[] vintToBytes(final long vint) {
        long i = vint;
        int size = WritableUtils.getVIntSize(i);
        byte[] result = new byte[size];
        int offset = 0;
        if (i >= -112 && i <= 127) {
            result[offset] = (byte) i;
            return result;
        }
        int len = -112;
        if (i < 0) {
            i ^= -1L;
            len = -120;
        }
        long tmp = i;
        while (tmp != 0) {
            tmp >>= 8;
            len--;
        }
        result[offset++] = (byte) len;
        len = (len < -120) ? -(len + 120) : -(len + 112);
        for (int idx = len; idx != 0; idx--) {
            int shiftbits = (idx - 1) * 8;
            long mask = 0xFFL << shiftbits;
            result[offset++] = (byte) ((i & mask) >> shiftbits);
        }
        return result;
    }

    public static long bytesToVint(final byte[] buffer) {
        int offset = 0;
        byte firstByte = buffer[offset++];
        int len = WritableUtils.decodeVIntSize(firstByte);
        if (len == 1) {
            return firstByte;
        }
        long i = 0;
        for (int idx = 0; idx < len - 1; idx++) {
            byte b = buffer[offset++];
            i = i << 8;
            i = i | (b & 0xFF);
        }
        return (WritableUtils.isNegativeVInt(firstByte) ? ~i : i);
    }

    public static long readVLong(final byte[] buffer, final int offset) throws IOException {
        byte firstByte = buffer[offset];
        int len = WritableUtils.decodeVIntSize(firstByte);
        if (len == 1) {
            return firstByte;
        }
        long i = 0;
        for (int idx = 0; idx < len - 1; idx++) {
            byte b = buffer[offset + 1 + idx];
            i = i << 8;
            i = i | (b & 0xFF);
        }
        return (WritableUtils.isNegativeVInt(firstByte) ? ~i : i);
    }

    public static int compareTo(final byte[] left, final byte[] right) {
        return compareTo(left, 0, left.length, right, 0, right.length);
    }

    public static int compareTo(byte[] buffer1, int offset1, int length1, byte[] buffer2, int offset2, int length2) {
        int end1 = offset1 + length1;
        int end2 = offset2 + length2;
        for (int i = offset1, j = offset2; i < end1 && j < end2; i++, j++) {
            int a = (buffer1[i] & 0xFF);
            int b = (buffer2[j] & 0xFF);
            if (a != b) {
                return a - b;
            }
        }
        return length1 - length2;
    }

    public static boolean equals(final byte[] left, final byte[] right) {
        if (left == null && right == null) {
            return true;
        }
        return (left == null || right == null || (left.length != right.length) ? false : compareTo(left, right) == 0);
    }

    public static boolean startsWith(byte[] bytes, byte[] prefix) {
        return bytes != null && prefix != null & bytes.length >= prefix.length && compareTo(bytes, 0, bytes.length, prefix, 0, prefix.length) == 0;
    }

    public static int hashCode(final byte[] b) {
        return hashCode(b, b.length);
    }

    public static int hashCode(final byte[] b, final int length) {
        return WritableComparator.hashBytes(b, length);
    }

    public static Integer mapKey(final byte[] b) {
        return hashCode(b);
    }

    public static Integer mapKey(final byte[] b, final int length) {
        return hashCode(b, length);
    }

    public static byte[] add(final byte[] a, final byte[] b) {
        return add(a, b, EMPTY_BYTE_ARRAY);
    }

    public static byte[] add(final byte[] a, final byte[] b, final byte[] c) {
        byte[] result = new byte[a.length + b.length + c.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        System.arraycopy(c, 0, result, a.length + b.length, c.length);
        return result;
    }

    public static byte[] head(final byte[] a, final int length) {
        if (a.length < length) {
            return null;
        }
        byte[] result = new byte[length];
        System.arraycopy(a, 0, result, 0, length);
        return result;
    }

    public static byte[] tail(final byte[] a, final int length) {
        if (a.length < length) {
            return null;
        }
        byte[] result = new byte[length];
        System.arraycopy(a, a.length - length, result, 0, length);
        return result;
    }

    public static byte[] padHead(final byte[] a, final int length) {
        byte[] padding = new byte[length];
        for (int i = 0; i < length; i++) {
            padding[i] = 0;
        }
        return add(padding, a);
    }

    public static byte[] padTail(final byte[] a, final int length) {
        byte[] padding = new byte[length];
        for (int i = 0; i < length; i++) {
            padding[i] = 0;
        }
        return add(a, padding);
    }

    public static byte[][] split(final byte[] a, final byte[] b, final int num) {
        byte[][] ret = new byte[num + 2][];
        int i = 0;
        Iterable<byte[]> iter = iterateOnSplits(a, b, num);
        if (iter == null) {
            return null;
        }
        for (byte[] elem : iter) {
            ret[i++] = elem;
        }
        return ret;
    }

    public static Iterable<byte[]> iterateOnSplits(final byte[] a, final byte[] b, final int num) {
        byte[] aPadded;
        byte[] bPadded;
        if (a.length < b.length) {
            aPadded = padTail(a, b.length - a.length);
            bPadded = b;
        } else if (b.length < a.length) {
            aPadded = a;
            bPadded = padTail(b, a.length - b.length);
        } else {
            aPadded = a;
            bPadded = b;
        }
        if (compareTo(aPadded, bPadded) >= 0) {
            throw new IllegalArgumentException("b <= a");
        }
        if (num <= 0) {
            throw new IllegalArgumentException("num cannot be < 0");
        }
        byte[] prependHeader = {1, 0};
        final BigInteger startBI = new BigInteger(add(prependHeader, aPadded));
        final BigInteger stopBI = new BigInteger(add(prependHeader, bPadded));
        final BigInteger diffBI = stopBI.subtract(startBI);
        final BigInteger splitsBI = BigInteger.valueOf(num + 1);
        if (diffBI.compareTo(splitsBI) < 0) {
            return null;
        }
        final BigInteger intervalBI;
        try {
            intervalBI = diffBI.divide(splitsBI);
        } catch (Exception e) {
            LOG.error("Exception caught during division", e);
            return null;
        }
        final Iterator<byte[]> iterator = new Iterator<byte[]>() {
            private int i = -1;

            @Override
            public boolean hasNext() {
                return i < num + 1;
            }

            @Override
            public byte[] next() {
                i++;
                if (i == 0) {
                    return a;
                }
                if (i == (num + 1)) {
                    return b;
                }
                BigInteger curBI = startBI.add(intervalBI.multiply(BigInteger.valueOf(i)));
                byte[] padded = curBI.toByteArray();
                if (padded[1] == 0) {
                    padded = tail(padded, padded.length - 2);
                } else {
                    padded = tail(padded, padded.length - 1);
                }
                return padded;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        return new Iterable<byte[]>() {
            @Override
            public Iterator<byte[]> iterator() {
                return iterator;
            }
        };
    }

    public static byte[][] toByteArrays(final String[] t) {
        byte[][] result = new byte[t.length][];
        for (int i = 0; i < t.length; i++) {
            result[i] = toBytes(t[i]);
        }
        return result;
    }

    public static byte[][] toByteArrays(final String column) {
        return toByteArrays(toBytes(column));
    }

    public static byte[][] toByteArrays(final byte[] column) {
        byte[][] result = new byte[1][];
        result[0] = column;
        return result;
    }

    public static int binarySearch(byte[][] arr, byte[] key, int offset, int length, RawComparator<byte[]> comparator) {
        int low = 0;
        int high = arr.length - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            int cmp = comparator.compare(key, offset, length, arr[mid], 0, arr[mid].length);
            if (cmp > 0) {
                low = mid + 1;
            } else if (cmp < 0) {
                high = mid - 1;
            } else {
                return mid;
            }
        }
        return -(low + 1);
    }

    public static byte[] incrementBytes(byte[] value, long amount) throws IOException {
        byte[] val = value;
        if (val.length < SIZEOF_LONG) {
            byte[] newvalue;
            if (val[0] < 0) {
                newvalue = new byte[]{-1, -1, -1, -1, -1, -1, -1, -1};
            } else {
                newvalue = new byte[SIZEOF_LONG];
            }
            System.arraycopy(val,0,newvalue,newvalue.length-val.length,val.length);
            val = newvalue;
        } else if (val.length > SIZEOF_LONG) {
            throw new IllegalArgumentException("Increment Bytes - value too big: " + val.length);
        }
        if (amount == 0) {
            return val;
        }
        if (val[0] < 0) {
            return binaryIncrementNeg(val, amount);
        }
        return binaryIncementPos(val, amount);
    }

    private static byte[] binaryIncementPos(byte[] value, long amount) {
        long amo = amount;
        int sign = 1;
        if (amount < 0) {
            amo = -amount;
            sign = -1;
        }
        for (int i = 0; i < value.length; i++) {
            int cur = ((int) amo % 256) * sign;
            amo = (amo >> 8);
            int val = value[value.length - i - 1] & 0xFF;
            int total = val + cur;
            if (total > 255) {
                amo += sign;
                total %= 256;
            }else if (total < 0) {
                amo -= sign;
            }
            value[value.length - i -1] = (byte) total;
            if (amo == 0) {
                return value;
            }
        }
        return value;
    }

    private static byte[] binaryIncrementNeg(byte[] value, long amount) {
        long amo = amount;
        int sign = 1;
        if (amount < 0) {
            amo = -amount;
            sign = -1;
        }
        for (int i = 0; i < value.length; i++) {
            int cur = ((int) amo % 256) * sign;
            amo = (amo >> 8);
            int val = ((~value[value.length - i - 1]) & 0xFF) + 1;
            int total = cur -val;
            if (total >= 0) {
                amo += sign;
            } else if (total < -256) {
                amo -= sign;
                total %= 256;
            }
            value[value.length - i - 1] = (byte) total;
            if (amo == 0) {
                return value;
            }
        }
        return value;
    }
}
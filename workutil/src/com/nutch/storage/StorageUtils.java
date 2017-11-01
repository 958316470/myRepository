package com.nutch.storage;

import java.nio.ByteBuffer;

public class StorageUtils {
    private static ByteBuffer deepCopyToReadOnlyBuffer(ByteBuffer input) {
        ByteBuffer copy = ByteBuffer.allocate(input.capacity());
        int position = input.position();
        input.reset();
        int mark = input.position();
        int limit = input.limit();
        input.rewind();
        input.limit(input.capacity());
        copy.put(input);
        input.rewind();
        copy.rewind();
        input.position(mark);
        input.mark();
        copy.position(mark);
        copy.mark();
        input.position(position);
        copy.position(position);
        input.limit(limit);
        copy.limit(limit);
        return copy.asReadOnlyBuffer();
    }
}

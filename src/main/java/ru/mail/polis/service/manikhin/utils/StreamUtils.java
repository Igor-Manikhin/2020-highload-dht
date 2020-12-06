package ru.mail.polis.service.manikhin.utils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public final class StreamUtils {
    private static final byte[] LF = "\n".getBytes(StandardCharsets.UTF_8);
    private static final byte[] CRLF = "\r\n".getBytes(StandardCharsets.UTF_8);
    private static final byte [] END_CHUNK_DATA = new byte[0];

    private StreamUtils(){
    }

    /**
     * Handler to create sending chunk in response by input record key and value.
     *
     * @param key - input key
     * @param value - input value
     */
    public static byte[] formFilledChunk(final ByteBuffer key, final ByteBuffer value) {
        final int dataLength = key.limit() + LF.length + value.limit();
        final byte[] hexLength = Integer.toHexString(dataLength)
                .getBytes(StandardCharsets.US_ASCII);
        final int chunkLength = dataLength + 2 * CRLF.length + hexLength.length;
        final ByteBuffer data = ByteBuffer.wrap(new byte[chunkLength]);

        data.put(hexLength);
        data.put(CRLF);
        data.put(key);
        data.put(LF);
        data.put(value);
        data.put(CRLF);

        return data.array();
    }

    /**
     * Handler for netty to create sending chunk in response by input record key and value.
     *
     * @param key - input key
     * @param value - input value
     */
    public static byte[] formNettyChunk(final ByteBuffer key, final ByteBuffer value) {
        final int dataLength = key.limit() + LF.length + value.limit();
        final ByteBuffer data = ByteBuffer.wrap(new byte[dataLength]);

        data.put(key);
        data.put(LF);
        data.put(value);

        return data.array();
    }

    /**
     * Handler for create end in sending response chunk by input record key and value.
     */
    public static byte[] formEndChunk() {
        final byte[] hexLength = Integer.toHexString(END_CHUNK_DATA.length)
                .getBytes(StandardCharsets.US_ASCII);
        final int chunkLength = END_CHUNK_DATA.length + 2 * CRLF.length + hexLength.length;
        final ByteBuffer chunk = ByteBuffer.wrap(new byte[chunkLength]);

        chunk.put(hexLength);
        chunk.put(CRLF);
        chunk.put(CRLF);

        return chunk.array();
    }
}

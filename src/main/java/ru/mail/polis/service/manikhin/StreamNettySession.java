package ru.mail.polis.service.manikhin;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.ChunkedStream;
import org.jetbrains.annotations.NotNull;
import ru.mail.polis.Record;
import ru.mail.polis.service.manikhin.utils.StreamUtils;
import java.util.Iterator;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class StreamNettySession extends ChannelDuplexHandler {
    private final Iterator<Record> iterator;

    /**
     * Stream netty service session.
     *
     * @param iterator - record iterator from DAO storage
     * */
    public StreamNettySession(final Iterator<Record> iterator) {
        this.iterator = iterator;
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);

        if (iterator != null) {
            stream(ctx);
        }
    }

    /**
     * Handle for running stream in session.
     *
     * @param ctx - channel handler context
     * */
    public void startStream(@NotNull final ChannelHandlerContext ctx) {
        final HttpResponse response = new DefaultHttpResponse(
                HTTP_1_1, HttpResponseStatus.OK
        );

        response.headers().set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
        ctx.writeAndFlush(response).isCancelled();
        stream(ctx);
    }

    private void stream(@NotNull final ChannelHandlerContext ctx) {
        while (iterator.hasNext()) {
            final Record record = iterator.next();
            final byte [] data = StreamUtils.formNettyChunk(record.getKey(), record.getValue());
            final ChunkedStream chunk = new ChunkedStream(new ByteBufInputStream(Unpooled.copiedBuffer(data)));

            ctx.writeAndFlush(chunk).isCancelled();
        }

        ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT).isCancelled();
    }
}

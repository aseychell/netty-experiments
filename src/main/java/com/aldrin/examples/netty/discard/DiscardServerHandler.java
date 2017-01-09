package com.aldrin.examples.netty.discard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

/**
 * @author <a href="mailto:aldrin.seychell@ixaris.com">aldrin.seychell</a>
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(DiscardServerHandler.class);
    private final StringBuffer sb = new StringBuffer();

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {

        final ByteBuf in = ((ByteBuf) msg);
        try {
            final String s = in.toString(CharsetUtil.US_ASCII);
            sb.append(s);
            if (sb.toString().endsWith(".")) {
                LOG.info("Received: {}", sb);
                sb.setLength(0);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        LOG.error("Exception caught", cause);
        ctx.close();
    }
}

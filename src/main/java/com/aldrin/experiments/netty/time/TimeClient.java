package com.aldrin.experiments.netty.time;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author <a href="mailto:aldrin.seychell@ixaris.com">aldrin.seychell</a>
 */
public class TimeClient {

    private static final Logger LOG = LoggerFactory.getLogger(TimeClient.class);
    private static final int LOW_WATERMARK = 8 * 1024;
    private static final int HIGH_WATERMARK = 32 * 1024;

    public static void main(final String[] args) throws Exception {

        final String host = args[0];
        final int port = Integer.parseInt(args[1]);
        final EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            final Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(final SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new TimeClientHandler());
                }
            });
            b.option(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(LOW_WATERMARK, HIGH_WATERMARK));

            final ChannelFuture f = b.connect(host, port);
            f.addListener(future -> LOG.info("Connected to server at {}:{}!", host, port));
            f.sync();
            // Wait until the connection is closed
            f.channel().closeFuture().sync();

        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}

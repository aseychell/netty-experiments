package com.aldrin.examples.netty.discard;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author <a href="mailto:aldrin.seychell@ixaris.com">aldrin.seychell</a>
 */
public class NettyDiscardServer {
    
    private final int port;
    
    public NettyDiscardServer(final int port) {
        this.port = port;
    }
    
    public void run() throws Exception {
        final EventLoopGroup bossGroup = new NioEventLoopGroup();
        final EventLoopGroup workerGroup = new NioEventLoopGroup();
        
        try {
            final ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    
                    @Override
                    protected void initChannel(final SocketChannel ch) throws Exception {
                         ch.pipeline().addLast(new DiscardServerHandler());
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128) // Socket backlog is the
                .childOption(ChannelOption.SO_KEEPALIVE, true);
            
            final ChannelFuture f = b.bind(port).sync();
            
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
        
    }
    
    public static void main(final String[] args) throws Exception {
        final int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }
        new NettyDiscardServer(port).run();
    }
    
}

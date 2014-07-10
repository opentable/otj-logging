package com.opentable.logging;

import java.net.InetSocketAddress;

import org.junit.rules.ExternalResource;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import redis.server.netty.RedisCommandDecoder;
import redis.server.netty.RedisCommandHandler;
import redis.server.netty.RedisReplyEncoder;
import redis.server.netty.SimpleRedisServer;

public class RedisServerRule extends ExternalResource
{

    private int port = -1;
    private ServerBootstrap b;
    private Channel channel;
    private DefaultEventExecutorGroup group;

    @Override
    protected void before() throws Throwable
    {
        // Only execute the command handler in a single thread
        final RedisCommandHandler commandHandler = new RedisCommandHandler(new SimpleRedisServer());

        b = new ServerBootstrap();
        group = new DefaultEventExecutorGroup(1);
        b.group(new NioEventLoopGroup(), new NioEventLoopGroup())
            .channel(NioServerSocketChannel.class)
            .option(ChannelOption.SO_BACKLOG, 100)
            .localAddress(0)
            .childOption(ChannelOption.TCP_NODELAY, true)
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    //                 p.addLast(new ByteLoggingHandler(LogLevel.INFO));
                    p.addLast(new RedisCommandDecoder());
                    p.addLast(new RedisReplyEncoder());
                    p.addLast(group, commandHandler);
                }
            });

        // Start the server.
        ChannelFuture f = b.bind().sync();

        channel = f.channel();
        port = ((InetSocketAddress)channel.localAddress()).getPort();
    }

    @Override
    protected void after()
    {
        group.shutdownGracefully();
    }

    public int getPort()
    {
        if (port <= 0) {
            throw new IllegalStateException("Didn't have a valid port: " + port);
        }
        return port;
    }
}

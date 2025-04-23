package com.hubis.acs.common.adapter.plc.server;

import com.hubis.acs.common.adapter.plc.handler.PLCDataHandler;
import com.hubis.acs.common.adapter.plc.handler.PLCServerHandler;
import com.hubis.acs.common.configuration.customAnnotation.EnableProtocol;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@EnableProtocol(protocol = "plc", server = true)
@Component
public class PLCServer {

    @Value("${plc.server.port}")
    private int port;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture serverChannelFuture;

    private final PLCDataHandler plcDataHandler;

    public PLCServer(PLCDataHandler plcDataHandler) {
        this.plcDataHandler = plcDataHandler;
    }

    @PostConstruct
    public void init(){
        startServer();
    }

    public void startServer() {
        new Thread(() -> {
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup();

            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .handler(new LoggingHandler(LogLevel.INFO))
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) {
                                ch.pipeline().addLast(new StringDecoder(StandardCharsets.UTF_8));
                                ch.pipeline().addLast(new StringEncoder(StandardCharsets.UTF_8));
                                ch.pipeline().addLast(new PLCServerHandler(plcDataHandler));
                            }
                        });

                serverChannelFuture = bootstrap.bind(port).sync();
                System.out.println("🚀 PLC Server started on port " + port);
                serverChannelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                shutdown();
            }
        }).start(); // ✅ 별도 스레드에서 실행하여 블로킹 방지
    }

    @PreDestroy
    public void shutdown() {
        if (bossGroup != null) bossGroup.shutdownGracefully();
        if (workerGroup != null) workerGroup.shutdownGracefully();
        System.out.println("🛑 PLC Server shut down.");
    }
}

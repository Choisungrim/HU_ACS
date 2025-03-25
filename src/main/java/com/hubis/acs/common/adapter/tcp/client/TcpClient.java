package com.hubis.acs.common.adapter.tcp.client;

import com.hubis.acs.common.adapter.tcp.handler.TcpClientHandler;
import com.hubis.acs.common.configuration.customAnnotation.EnableProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@EnableProtocol(protocol = "tcp", server = false)
@Component
public class TcpClient {

    @Value("${tcp.client.host}")
    private String host;

    @Value("${tcp.client.port}")
    private int port;

    private EventLoopGroup group;
    private Channel channel;

    @PostConstruct
    public void init() {
        startClient(); // ✅ 여기서 Netty 실행
    }

    public void startClient() {
        new Thread(this::connect).start(); // ✅ 별도 스레드에서 실행
    }

    private void connect() {
        group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline().addLast(new StringDecoder(StandardCharsets.UTF_8));
                        ch.pipeline().addLast(new StringEncoder(StandardCharsets.UTF_8));
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                        ch.pipeline().addLast(new TcpClientHandler());
                    }
                });

        while (true) {
            try {
                ChannelFuture future = bootstrap.connect(host, port).sync();
                channel = future.channel();
                System.out.println("🔗 Connected to TCP Server at " + host + ":" + port);
                channel.closeFuture().sync(); // 연결 유지
            } catch (Exception e) {
                System.out.println("⚠️ Connection lost. Reconnecting in 5 seconds...");
                try {
                    Thread.sleep(5000); // 재연결 대기
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    public void sendMessage(String message) {
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(message);
        }
    }

    @PreDestroy
    public void shutdown() {
        if (group != null) group.shutdownGracefully();
        System.out.println("🛑 TCP Client shut down.");
    }
}

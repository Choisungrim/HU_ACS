package com.hubis.acs.common.adapter.plc.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hubis.acs.common.adapter.plc.handler.PLCClientHandler;
import com.hubis.acs.common.configuration.customAnnotation.EnableProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@EnableProtocol(protocol = "plc", server = false)
//@Component
public class PLCClient {

    @Value("${plc.client.host}")
    private String host;

    @Value("${plc.client.port}")
    private int port;

    @Value("${plc.data.format}")
    private String dataFormat;

    private EventLoopGroup group;
    private Channel channel;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @EventListener(ApplicationReadyEvent.class) // ✅ Spring 컨텍스트 로드 완료 후 실행
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
                        ch.pipeline().addLast(new PLCClientHandler());
                    }
                });

        while (true) {
            try {
                ChannelFuture future = bootstrap.connect(host, port).sync();
                channel = future.channel();
                System.out.println("🔗 Connected to PLC Server at " + host + ":" + port);
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

    public void sendCommand(String status) {
        String message;
        switch (dataFormat.toLowerCase()) {
            case "xml":
                message = "<plc><status>" + status + "</status></plc>";
                break;
            case "json":
                Map<String, String> jsonMap = new HashMap<>();
                jsonMap.put("status", status);
                try {
                    message = objectMapper.writeValueAsString(jsonMap);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                break;
            case "modbus":
                message = "ON".equalsIgnoreCase(status) ? "0x01" : "0x00";
                break;
            default:
                return;
        }
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(message);
        }
    }

    @PreDestroy
    public void shutdown() {
        if (group != null) group.shutdownGracefully();
        System.out.println("🛑 PLC Client shut down.");
    }
}

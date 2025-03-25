package com.hubis.acs.common.adapter.plc.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.stereotype.Component;

@Component
public class PLCServerHandler extends SimpleChannelInboundHandler<String> {

    private final PLCDataHandler plcDataHandler;

    public PLCServerHandler(PLCDataHandler plcDataHandler) {
        this.plcDataHandler = plcDataHandler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        System.out.println("Received PLC Data: " + msg);

        // 클라이언트의 데이터 형식(XML, JSON, Modbus)을 유지한 채 응답 처리
        plcDataHandler.parseData(msg).ifPresent(response -> {
            String formattedResponse = plcDataHandler.formatResponse(response);
            System.out.println("Processed & Formatted Data: " + formattedResponse);
            ctx.writeAndFlush(formattedResponse);
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}

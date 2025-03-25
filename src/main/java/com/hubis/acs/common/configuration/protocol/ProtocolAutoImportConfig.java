package com.hubis.acs.common.configuration.protocol;

import com.hubis.acs.common.adapter.plc.client.PLCClient;
import com.hubis.acs.common.adapter.plc.server.PLCServer;
import com.hubis.acs.common.adapter.tcp.client.TcpClient;
import com.hubis.acs.common.adapter.tcp.server.TcpServer;
import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        TcpClient.class,
        TcpServer.class,
        PLCClient.class,
        PLCServer.class
        // 추가 가능
})
public class ProtocolAutoImportConfig {}
package com.hubis.acs.common.configuration.protocol;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Component;

import java.util.*;

@ConfigurationProperties(prefix = "protocol")
@Component
public class ProtocolConfig {

    private List<String> protocols;
    private List<String> protocolModes;

    private final Map<String, String> protocolModeMap = new HashMap<>();

    @PostConstruct
    public void init() {
        if (protocols == null || protocolModes == null || protocols.size() != protocolModes.size()) {
            throw new IllegalStateException("❌ Mismatch between protocols and protocolModes!");
        }

        for (int i = 0; i < protocols.size(); i++) {
            protocolModeMap.put(protocols.get(i).trim(), protocolModes.get(i).trim().toLowerCase());
        }

        System.out.println("✅ ProtocolConfig Initialized - " + protocolModeMap);
    }

    // getter/setter 필수!
    public List<String> getProtocols() { return protocols; }
    public void setProtocols(List<String> protocols) { this.protocols = protocols; }

    public List<String> getProtocolModes() { return protocolModes; }
    public void setProtocolModes(List<String> protocolModes) { this.protocolModes = protocolModes; }

    public boolean isProtocolEnabled(String protocol) {
        return protocolModeMap.containsKey(protocol);
    }

    public boolean isServerMode(String protocol) {
        return "server".equalsIgnoreCase(protocolModeMap.get(protocol));
    }

    public boolean isClientMode(String protocol) {
        return "client".equalsIgnoreCase(protocolModeMap.get(protocol));
    }

    public static boolean checkCondition(ConditionContext context, AnnotatedTypeMetadata metadata, String protocol, boolean isServer) {
        ProtocolConfig config = context.getBeanFactory().getBean(ProtocolConfig.class);
        return config.isProtocolEnabled(protocol) &&
                ((isServer && config.isServerMode(protocol)) || (!isServer && config.isClientMode(protocol)));
    }
}

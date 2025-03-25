package com.hubis.acs.common.configuration.protocol;

import com.hubis.acs.common.configuration.customAnnotation.EnableProtocol;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProtocolCondition implements Condition {

    private static final Map<String, Boolean> cache = new ConcurrentHashMap<>();

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, Object> attributes = metadata.getAnnotationAttributes(EnableProtocol.class.getName());

        if (attributes == null) return false;

        String protocol = (String) attributes.get("protocol");
        boolean isServer = (boolean) attributes.get("server");

        String key = protocol + "|" + isServer;

        // ✅ 중복 호출 방지
        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        // ✅ Environment에서 직접 가져오기
        Environment env = context.getEnvironment();

        String protocolList = env.getProperty("protocol.protocols");
        String modeList = env.getProperty("protocol.protocolModes");

        if (protocolList == null || modeList == null) {
            System.out.println("⚠️ Protocol list or mode list is missing in properties.");
            return false;
        }

        List<String> protocols = Arrays.stream(protocolList.split(","))
                .map(String::trim)
                .toList();

        List<String> modes = Arrays.stream(modeList.split(","))
                .map(String::trim)
                .toList();

        int idx = protocols.indexOf(protocol);
        if (idx == -1) {
            System.out.println("⛔️ Protocol '" + protocol + "' is not enabled.");
            cache.put(key, false);
            return false;
        }

        String mode = modes.size() > idx ? modes.get(idx).toLowerCase() : "";
        boolean result = (isServer && "server".equals(mode)) || (!isServer && "client".equals(mode));

        System.out.printf("✅ Condition result for %s | mode: %s | isServer: %s → %s%n",
                protocol, mode, isServer, result);

        cache.put(key, result); // ✅ 캐시 저장

        return result;
    }
}


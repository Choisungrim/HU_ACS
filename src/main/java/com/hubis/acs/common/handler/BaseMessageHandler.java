package com.hubis.acs.common.handler;

import com.hubis.acs.middleware.MiddlewareMessageHandler;
import com.hubis.acs.ui.UiMessageHandler;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BaseMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(BaseMessageHandler.class);

    private final MiddlewareMessageHandler middlewareMessageHandler;
    private final UiMessageHandler uiMessageHandler;

    public void handle(Message<?> msg, String client) {
        String topic = msg.getHeaders().get("mqtt_receivedTopic").toString();
        if(client.toLowerCase().equals("mqtt"))
        {
            if(topic.startsWith("web"))
                uiMessageHandler.handle(topic, msg);
            else if(topic.startsWith("middleware"))
                middlewareMessageHandler.handle(topic, msg);
        }
    }
}

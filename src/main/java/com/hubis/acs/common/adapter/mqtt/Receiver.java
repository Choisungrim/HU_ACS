package com.hubis.acs.common.adapter.mqtt;

import com.hubis.acs.common.handler.BaseMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class Receiver {

    private final BaseMessageHandler baseMessageHandler;

    @Autowired
    public Receiver(BaseMessageHandler baseMessageHandler) { this.baseMessageHandler = baseMessageHandler; }

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void mqttMessageHandler(Message<?> message) {
        System.out.println("Received: " + message);
        baseMessageHandler.handle(message,"MQTT");
    }

}

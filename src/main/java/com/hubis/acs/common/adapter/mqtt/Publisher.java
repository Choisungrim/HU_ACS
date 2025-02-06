package com.hubis.acs.common.adapter.mqtt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class Publisher {

    private final MessageHandler mqttHandler;

    @Autowired
    public Publisher(@Qualifier("mqttOutbound")MessageHandler  mqttHandler) {
        this.mqttHandler = mqttHandler;
    }

    public void publish(String topic, String message) {
        Message<?> mqttMessage = MessageBuilder.withPayload(message)
                .setHeader(MqttHeaders.TOPIC, topic)
                .build();

        try {
            mqttHandler.handleMessage(mqttMessage); // MqttPahoMessageHandler를 사용하여 메시지를 발송
        } catch (Exception e) {
            e.printStackTrace(); // 예외 처리
        }
    }


}

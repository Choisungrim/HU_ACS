package com.hubis.acs.common.adapter.mqtt;

import com.hubis.acs.common.adapter.mqtt.Publisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MqttPingScheduler {

    private final Publisher publisher;

    public MqttPingScheduler(Publisher publisher) {
        this.publisher = publisher;
    }

    // 1초마다 실행 (1000ms)
    @Scheduled(fixedRate = 1000)
    public void sendPing() {
        String topic = "itk/test/ping"; // 원하는 토픽으로 변경
        String message = "ping";
        publisher.publish(topic, message);
//        System.out.println("📡 Sent ping message to topic: " + topic);
    }
}


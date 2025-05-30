package com.hubis.acs.common.adapter.mqtt;

import com.hubis.acs.common.configuration.MqttConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class Publisher {

    private final MessageHandler mqttHandler;
    private final MqttConfig mqttConfig;
    private volatile boolean reconnecting = false;  // 중복 루프 방지용 플래그
    private volatile boolean lastConnected = true;

    public boolean isLastConnected() {
        return lastConnected;
    }


    @Autowired
    public Publisher(@Qualifier("mqttOutbound")MessageHandler  mqttHandler,MqttConfig mqttConfig) {
        this.mqttHandler = mqttHandler;
        this.mqttConfig = mqttConfig;
    }

    public void publish(String topic, String message) {
        Message<?> mqttMessage = MessageBuilder.withPayload(message)
                .setHeader(MqttHeaders.TOPIC, topic)
                .build();

        try {
            mqttHandler.handleMessage(mqttMessage); // MqttPahoMessageHandler를 사용하여 메시지를 발송
            lastConnected = true;
        } catch (Exception e) {
            System.err.println("❌ MQTT Publish 실패: "+ e.getMessage());
            lastConnected = false;
            if (!mqttConfig.isMqttConnected()) {
                //상태체크 메소드 생성
                startReconnectWatcher();
            }
        }
    }

    private void startReconnectWatcher() {
        if (reconnecting) return;
        reconnecting = true;

        new Thread(() -> {
            System.out.println("📡 MQTT 재연결 감시 시작...");

            while (true) {
                try {
                    Thread.sleep(500);  // 0.5초 간격
                    if (mqttConfig.isMqttConnected()) {
                        System.out.println("✅ MQTT 재연결 완료!");
                        reconnecting = false;
                        break;
                    } else {
                        System.out.print("."); // 계속 감시 중 시각적 표시
                    }

                    if (isLastConnected()) {
                        System.out.println("✅ MQTT 재연결 완료!");
                        reconnecting = false;
                        break;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception ex) {
                    System.err.println("MQTT 상태 확인 실패: " + ex.getMessage());
                }
            }
        }, "MqttReconnectWatcher").start();
    }

}

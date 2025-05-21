package com.hubis.acs.common.configuration;

import lombok.Getter;
import lombok.Setter;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageChannel;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "mqtt")
public class MqttConfig {

    private String url;  // application.properties에서 mqtt.url 매핑
    private String username;
    private String password;
    private String topic;
    private String clientId;

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(setMqttConnectOptions());
        return factory;
    }

    private MqttConnectOptions setMqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{url}); // tcp:// 포함된 값이 application.properties에 있어야 함
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setConnectionTimeout(100);
        options.setKeepAliveInterval(600);
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setMaxReconnectDelay(10);
        options.setMaxInflight(300);

        return options;
    }

    @Bean
    public MqttPahoMessageDrivenChannelAdapter mqttInbound() {
        String[] topics = validateTopic(topic);
        System.out.println(topics);
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(clientId + "_Subscribe", mqttClientFactory(), topics);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MqttPahoMessageHandler mqttOutbound() {
        MqttPahoMessageHandler handler = new MqttPahoMessageHandler(clientId + "_Publisher", mqttClientFactory());
        handler.setAsync(true);
        handler.setDefaultQos(1);
        return handler;
    }

    @Bean
    public MessageChannel mqttOutputChannel() {
        return new DirectChannel();
    }

    /**
     * MQTT 토픽 검증 및 기본값 설정
     */
    private String[] validateTopic(String topic) {
        if (topic == null || topic.trim().isEmpty()) {
            return new String[]{"#"}; // 기본값 설정
        }
        return topic.contains(",") ? topic.split(",") : new String[]{topic}; // 여러 개의 토픽을 배열로 반환
    }
}

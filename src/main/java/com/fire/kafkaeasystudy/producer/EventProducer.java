package com.fire.kafkaeasystudy.producer;

import jakarta.annotation.Resource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {

    // 加入了spring-kafka依赖 + .yml配置信息，springboot自动配置好了kafka，自动装配好了KafkaTemplate这个Bean
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendEvent() {
        kafkaTemplate.send("hello-topic","hello kafka");
    }

    public void sendEvent2(){
        // 通过构建器模式创建Message对象
        Message<String> message = MessageBuilder.withPayload("hello kafka message").setHeader(KafkaHeaders.TOPIC,"test-topic") // 在header中设置topic的名字
                .build();
        kafkaTemplate.send(message);
    }


}

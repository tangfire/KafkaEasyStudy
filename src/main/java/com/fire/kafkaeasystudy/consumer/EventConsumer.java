package com.fire.kafkaeasystudy.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventConsumer {
    // 采用监听的方式接收事件(消息、数据)
    @KafkaListener(topics = {"hello-topic"},groupId = "hello-group")
    public void onEvent(String event) {
        System.out.println("读取的事件："+event);
    }
}

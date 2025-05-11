package com.fire.kafkaeasystudy.producer;

import com.fire.kafkaeasystudy.model.User;
import jakarta.annotation.Resource;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class EventProducer {

    // 加入了spring-kafka依赖 + .yml配置信息，springboot自动配置好了kafka，自动装配好了KafkaTemplate这个Bean
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate2;

    public void sendEvent() {
        kafkaTemplate.send("hello-topic","hello kafka");
    }

    public void sendEvent2(){
        // 通过构建器模式创建Message对象
        Message<String> message = MessageBuilder.withPayload("hello kafka message").setHeader(KafkaHeaders.TOPIC,"test-topic") // 在header中设置topic的名字
                .build();
        kafkaTemplate.send(message);
    }

    public void sendEvent3(){
        // Headers里面是放一些信息(信息是key-value键值对)，到时候消费者接收到该消息后，可以拿到这个Headers里面放的信息
        Headers headers = new RecordHeaders();
        headers.add("phone","133123214".getBytes(StandardCharsets.UTF_8));
        headers.add("orderId","OD123123412441".getBytes(StandardCharsets.UTF_8));
//        public ProducerRecord(String topic, Integer partition, Long timestamp, K key, V value, Iterable< Header > headers) {
        ProducerRecord<String, String> record = new ProducerRecord<>("test-topic-02",0,System.currentTimeMillis(),"k1","hello kafka",headers);
        kafkaTemplate.send(record);
    }


    public void sendEvent4(){
//        CompletableFuture<SendResult<K, V>> send(String topic, Integer partition, Long timestamp, K key, V data);
        kafkaTemplate.send("test-topic-02",0,System.currentTimeMillis(),"k2","hello kafka");

    }

    public void sendEvent5(){
//        CompletableFuture<SendResult<K, V>> sendDefault(Integer partition, Long timestamp, K key, V data);
//            # 配置模版默认的主题topic名称
//            template:
//                default-topic: default-topic
        kafkaTemplate.sendDefault(0,System.currentTimeMillis(),"k3","hello kafka");
    }

    public void sendEvent6(){
        CompletableFuture<SendResult<String, String>> completableFuture
                = kafkaTemplate.sendDefault(0, System.currentTimeMillis(), "k3", "hello kafka");
        // 怎么拿到结果，通过CompletableFuture这个类拿结果，这个类里面有很多方法
        try {
            // 1. 阻塞等待的方式拿结果
            SendResult<String, String> sendResult = completableFuture.get();

            if (sendResult.getRecordMetadata() != null){
                // kafka服务器确认已经接收到了消息
                System.out.println("消息发送成功："+sendResult.getRecordMetadata().toString());
            }

            System.out.println("producerRecord = "+sendResult.getProducerRecord());


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void sendEvent7(){
        CompletableFuture<SendResult<String, String>> completableFuture
                = kafkaTemplate.sendDefault(0, System.currentTimeMillis(), "k3", "hello kafka");

        try {
            // 2. 非阻塞等待的方式拿结果
            completableFuture.thenAccept(sendResult ->{
                if (sendResult.getRecordMetadata() != null){
                    // kafka服务器确认已经接收到了消息
                    System.out.println("消息发送成功："+sendResult.getRecordMetadata().toString());
                }

                System.out.println("producerRecord = "+sendResult.getProducerRecord());
            }).exceptionally(t->{
                t.printStackTrace();
                // 失败处理
                return null;
            });


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void sendEvent8(){
        User user  = User.builder().id(1208).phone("1334124124").birthday(new Date()).build();
        // 分区是null，让kafka自己去决定把消息发到哪个分区

//        producer:
//            value-serializer: org.springframework.kafka.support.serializer.ToStringSerializer
//            value-serializer: org.springframework.kafka.support.serializer.JsonSerializer

//        <dependency>
//            <groupId>com.fasterxml.jackson.core</groupId>
//            <artifactId>jackson-databind</artifactId>
//            <version>2.15.2</version>
//        </dependency>


        kafkaTemplate2.sendDefault(null,System.currentTimeMillis(),"k4",user);
    }

    /**
     * 看config.KafkaConfig，使用了我们自定义的分区策略，轮询分配策略
     * config.KafkaConfig:
     * props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, RoundRobinPartitioner.class);
     */
    public void sendEvent9(){
        User user  = User.builder().id(1208).phone("1334124124").birthday(new Date()).build();
        kafkaTemplate2.send("firetopic",user);
    }




}

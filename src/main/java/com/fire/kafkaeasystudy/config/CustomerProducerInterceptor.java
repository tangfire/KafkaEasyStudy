package com.fire.kafkaeasystudy.config;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;

public class CustomerProducerInterceptor implements ProducerInterceptor<String,Object> {

    /**
     * 发送消息时，会先调用该方法，对消息进行拦截，可以在拦截中对消息做一些处理，记录日志等操作......
     * @param record the record from client or the record returned by the previous interceptor in the chain of interceptors.
     * @return
     */
    @Override
    public ProducerRecord<String,Object> onSend(ProducerRecord record) {
        System.out.println("拦截消息" + record.toString());
        return record;
    }

    /**
     * 服务器收到消息后的一个确认
     * @param metadata The metadata for the record that was sent (i.e. the partition and offset).
     *                 If an error occurred, metadata will contain only valid topic and maybe
     *                 partition. If partition is not given in ProducerRecord and an error occurs
     *                 before partition gets assigned, then partition will be set to RecordMetadata.NO_PARTITION.
     *                 The metadata may be null if the client passed null record to
     *                 {@link org.apache.kafka.clients.producer.KafkaProducer#send(ProducerRecord)}.
     * @param exception The exception thrown during processing of this record. Null if no error occurred.
     */
    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        if (metadata != null) {
            System.out.println("服务器收到该消息: " + metadata.offset());
        }else{
            System.out.println("消息发送失败了,exception:" + exception.getMessage());
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}

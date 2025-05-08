package com.fire.kafkaeasystudy;

import com.fire.kafkaeasystudy.producer.EventProducer;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootTest
class KafkaEasyStudyApplicationTests {

    @Resource
    private EventProducer eventProducer;

    @Test
    void test01() {
        eventProducer.sendEvent();
    }

    @Test
    void test02() {
        eventProducer.sendEvent2();
    }



}

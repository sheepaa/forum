package com.newcoder.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class KafkaTest {
    @Autowired
    KafkaProducer kafkaProducer;

    @Autowired
    KafkaConsumer kafkaConsumer;

    @Test
    public void testKafka() throws InterruptedException {
        kafkaProducer.sendMessage("test", "hello");
        kafkaProducer.sendMessage("test", "nihao");

        Thread.sleep(100*1000);
    }
}

@Component
class KafkaProducer{
    @Autowired
    private KafkaTemplate kafkaTemplate;
    public void sendMessage(String topic, String content){
        kafkaTemplate.send(topic, content);
    }
}

@Component
class KafkaConsumer{
    @Autowired
    private KafkaTemplate kafkaTemplate;

    @KafkaListener(topics = {"test"})
    public void handle(ConsumerRecord consumerRecord){
        String value = (String) consumerRecord.value();
        System.out.println(value);
    }

}

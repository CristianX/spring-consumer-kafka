package gob.mdmq.springconsumerkafka.controller;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Consumer {
    private static final String orderTopic = "smsr";
    
    @KafkaListener(topics = {orderTopic})
    public void consumeMessage(String message) throws JsonProcessingException {
        log.info("message consumed {}", message);

    }

}

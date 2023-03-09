package gob.mdmq.springconsumerkafka.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoTypeMapper;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;


@Configuration
public class KafkaConsumerConfig {

    @Bean
    public MongoTypeMapper mongoTypeMapper() {
        return null;
        // return new MongoTypeMapper(null);
    }
}

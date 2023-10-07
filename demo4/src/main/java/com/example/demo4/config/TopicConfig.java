package com.example.demo4.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Denna klass ör en konfigurationsklass för att skapa Kafka-ämnen.
 * Innehåller en metod NewTopic som skapar kafka-ämnet med namnet "javaJsonGuides"
 * @return
 */
@Configuration
public class TopicConfig {



    @Bean
    public NewTopic myJsonTopic() { return TopicBuilder.name("javaJsonGuides").build(); }
}

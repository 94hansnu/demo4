package com.example.demo4.kafka;

import com.example.demo4.payload.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class JsonKafkaProducer {

    private KafkaTemplate<String, User> kafkaTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger((JsonKafkaProducer.class));

    public JsonKafkaProducer(KafkaTemplate<String, User> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(User data) {
        LOGGER.info(String.format("Message sent -> %s", data.toString()));

        Message<User> message = MessageBuilder.withPayload(data).setHeader(KafkaHeaders.TOPIC, "javaJsonGuides").build();

        kafkaTemplate.send(message);
    }

    public void sendAddUserEvent(User data) {
        LOGGER.info("Sending add user event for user: " + data.toString());

        // Skapa ett meddelande för att indikera att en ny användare har lagts till och skicka det till Kafka
        Message<User> message = MessageBuilder
                .withPayload(data)
                .setHeader(KafkaHeaders.TOPIC, "javaJsonGuides") // Byt ut "addUserTopic" mot ditt faktiska ämnesnamn
                .build();

        kafkaTemplate.send(message);
    }

    public void sendUpdateEvent(User data) {
        LOGGER.info("Sending update event for user: " + data.toString());

        // Skapa ett meddelande för uppdatering och skicka det till Kafka
        Message<User> message = MessageBuilder
                .withPayload(data)
                .setHeader(KafkaHeaders.TOPIC, "javaJsonGuides") // Byt ut "updateUserTopic" mot ditt faktiska ämnesnamn
                .build();

        kafkaTemplate.send(message);
    }

    public void sendDeleteEvent(Long userId) {
        LOGGER.info("Sending delete event for user: " + userId);

        // Skapa ett meddelande för radering och skicka det till Kafka
        Message<Long> message = MessageBuilder
                .withPayload(userId)
                .setHeader(KafkaHeaders.TOPIC, "javaJsonGuides") // Byt ut "deleteUserTopic" mot ditt faktiska ämnesnamn
                .build();

        kafkaTemplate.send(message);
    }


}

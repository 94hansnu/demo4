package com.example.demo4.kafka;
import com.example.demo4.payload.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * Detta är en Kafka Producer-tjänst som används för att skicka JSON-data till ett Kafka-ämne.
 * Metoden sendMessage skickar ett JSON-meddelande till Kafka, loggar det skickade händelsen och skapar ett meddelande
 * och skickar det till Kafka.
 * Metoden sendAddUserEvent skickar en händelse om att en ny användare har lagts till, loggar händelsen och skapar
 * ett meddelande och skickar det till kafka.
 * Metoden sendUpdateEvent skickar en uppdateringshändelse om en användare, loggar händelsen och skapar ett meddelande
 * och skickar det till Kafka.
 * Metoden sendDeleteEvent skicka en raderingshändelse för en användare, loggar händelsen och skapar ett meddelande
 * och skickar det till Kafka.
 */
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

        Message<User> message = MessageBuilder
                .withPayload(data)
                .setHeader(KafkaHeaders.TOPIC, "javaJsonGuides")
                .build();

        kafkaTemplate.send(message);
    }

    public void sendUpdateEvent(User data) {
        LOGGER.info("Sending update event for user: " + data.toString());

        Message<User> message = MessageBuilder
                .withPayload(data)
                .setHeader(KafkaHeaders.TOPIC, "javaJsonGuides")
                .build();

        kafkaTemplate.send(message);
    }

    public void sendDeleteEvent(Long userId) {
        LOGGER.info("Sending delete event for user: " + userId);

        Message<Long> message = MessageBuilder
                .withPayload(userId)
                .setHeader(KafkaHeaders.TOPIC, "javaJsonGuides")
                .build();

        kafkaTemplate.send(message);
    }


}

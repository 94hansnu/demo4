package com.example.demo4.kafka;


import com.example.demo4.payload.User;
import com.example.demo4.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Detta är en Kafka Consumer-tjänst som lyssnar på ett Kafka-ämne och hanterar inkommande JSON-meddelanden.
 * Metod som är märkt som en Kafka Listener och är konfigurerad för att konsumera meddelanden från "javaJsonGuides"-ämnet,
 * inkommande meddelanden (User-objekt) bearbetas och loggar det inkommande JSON-meddelandet.
 */
@Service
public class JsonKafkaConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonKafkaConsumer.class);

    @Autowired
    private UserRepository userRepository;

    @KafkaListener(topics = "javaJsonGuides", groupId = "myGroup")
    public void consume(User user) {

        LOGGER.info(String.format("Json message received -> %s", user.toString()));

    }
}

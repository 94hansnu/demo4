package com.example.demo4.kafka;
import com.example.demo4.payload.User;
import com.example.demo4.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Detta är en Kafka Consumer-tjänst som lyssnar på ett Kafka-ämne och sparar inkommande JSON-data till en databas.
 * Metod som är märkt som en Kafka Listener och är konfigurerad för att konsumera meddelanden från "javaJsonGuides"-ämnet,
 * inkommande meddelanden (User-objekt) sparas, skriver ut det inkommande JSON-meddelandet och sparar i databasen.
 */
@Service
public class JsonKafkaConsumerDb {

    @Autowired
    private UserRepository userRepository;

    @KafkaListener(topics = "javaJsonGuides", groupId = "otherGroup")
    public void writeToDb(User user) {

        System.out.println(user);
        System.out.println("Skickar data till DB!");
        userRepository.save(user);
    }
}

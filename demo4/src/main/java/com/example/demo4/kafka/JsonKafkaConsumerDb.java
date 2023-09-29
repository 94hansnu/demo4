package com.example.demo4.kafka;


import com.example.demo4.payload.User;
import com.example.demo4.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class JsonKafkaConsumerDb {

    @Autowired
    private UserRepository userRepository;

    @KafkaListener(topics = "javaJsonGuides", groupId = "otherGroup")
    public void writeToDb(User user) {

        System.out.println(user);

        System.out.println("Skickar data till DB!");
        //Skicka data till DB
        userRepository.save(user);
    }
}

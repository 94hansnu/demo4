package com.example.demo4.controller;

import com.example.demo4.kafka.JsonKafkaProducer;
import com.example.demo4.payload.User;
import com.example.demo4.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Detta är en controllerklass som hanterar HTTP-förfrågningar relaterade till JSON-meddelanden och användare.
 * Konstruktor för klassen som tar emot en Kafka-producent och en användarservice.
 * Metod för att publicera en JSON till Kafka och returnera ett svar.
 * Metod för att lägga till en användare i databasen och skicka en händelse till Kafka.
 * Metod för att uppdatera en användare i databasen och skicka en uppdateringshändelse till Kafka.
 * Metod för att ta bort en användare från databasen och skicka en raderingshändelse till Kafka.
 */

@RestController
@RequestMapping("/api/v1/kafka")
public class JsonMessageController {
    private JsonKafkaProducer kafkaProducer;
    private UserService userService;
    private static final Logger LOGGER = LoggerFactory.getLogger((JsonMessageController.class));

    public JsonMessageController(JsonKafkaProducer kafkaProducer, UserService userService) {
        this.kafkaProducer = kafkaProducer;
        this.userService = userService;
    }

    @PostMapping("/publish")
    public ResponseEntity<String> publish(@RequestBody User user) {
        kafkaProducer.sendMessage(user);
        return ResponseEntity.ok("Json Message send to Kafka Topic");
    }


    @PostMapping("/addUser")
    public ResponseEntity<String> addUser(@RequestBody User user) {

        try {
            User addedUser = userService.addUser(user);
            LOGGER.info("User added: " + addedUser);

            kafkaProducer.sendAddUserEvent(addedUser);

            return ResponseEntity.ok("User added successfully. ");
        } catch (Exception e) {
            LOGGER.error("Failed to add user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add user");
        }
    }

    @PostMapping("/updateUser")
    public ResponseEntity<String> updateUser(@RequestBody User user) {
        try {
            userService.updateUser(user);
            LOGGER.info("User updated: " + user);

            kafkaProducer.sendUpdateEvent(user);

            return ResponseEntity.ok("User updated successfully.");
        } catch (Exception e) {
            LOGGER.error("Failed to update user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update user");
        }
    }

    @DeleteMapping("/deleteUser/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            LOGGER.info("User deleted: " + userId);

            kafkaProducer.sendDeleteEvent(userId);

            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            LOGGER.error("Failed to delete user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete user");
        }
    }
}





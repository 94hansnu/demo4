package com.example.demo4.controller;

import com.example.demo4.kafka.JsonKafkaProducer;
import com.example.demo4.payload.User;
import com.example.demo4.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            User addedUser = userService.addUser(user); // Lägg till användaren i databasen
            LOGGER.info("User added: " + addedUser);

            // Skicka en händelse till Kafka om den nya användaren
            kafkaProducer.sendAddUserEvent(addedUser);

            return ResponseEntity.ok("User added successfully");
        } catch (Exception e) {
            LOGGER.error("Failed to add user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add user");
        }
    }

    @PostMapping("/updateUser")
    public ResponseEntity<String> updateUser(@RequestBody User user) {
        try {
            userService.updateUser(user); // Uppdatera användaren i databasen
            LOGGER.info("User updated: " + user);

            // Skicka en uppdateringshändelse till Kafka
            kafkaProducer.sendUpdateEvent(user);

            return ResponseEntity.ok("User updated successfully");
        } catch (Exception e) {
            LOGGER.error("Failed to update user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update user");
        }
    }

    @DeleteMapping("/deleteUser/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId); // Ta bort användaren från databasen
            LOGGER.info("User deleted: " + userId);

            // Skicka en raderingshändelse till Kafka
            kafkaProducer.sendDeleteEvent(userId);

            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            LOGGER.error("Failed to delete user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete user");
        }
    }
}

/*    @DeleteMapping("/deleteUser/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        // Här kan du hantera borttagning av användaren från din databas eller annan lagringsplats.
        // Använd 'userId' för att identifiera vilken användare som ska raderas.

        try {
            userService.deleteUser(userId); // Anropa din användarens tjänst för att radera användaren
            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete user");
        }
    }*/




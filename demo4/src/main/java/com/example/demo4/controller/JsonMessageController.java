package com.example.demo4.controller;

import com.example.demo4.kafka.JsonKafkaProducer;
import com.example.demo4.payload.User;
import com.example.demo4.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/kafka")
public class JsonMessageController {
    private JsonKafkaProducer kafkaProducer;
    private UserService userService;

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
        // Här kan du hantera att lägga till användaren i din databas eller annan lagringsplats.
        // Använd 'user' objektet som innehåller användardata.

        try {
            userService.addUser(user); // Anropa din användarens tjänst för att lägga till användaren
            return ResponseEntity.ok("User added successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add user");
        }
    }

    @PostMapping("/updateUser")
    public ResponseEntity<String> updateUser(@RequestBody User user) {
        // Här kan du hantera uppdatering av användaren i din databas eller annan lagringsplats.
        // Använd 'user' objektet som innehåller användardata för uppdateringen.

        try {
            userService.updateUser(user); // Anropa din användarens tjänst för att uppdatera användaren
            return ResponseEntity.ok("User updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update user");
        }
    }

    @DeleteMapping("/deleteUser/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        // Här kan du hantera borttagning av användaren från din databas eller annan lagringsplats.
        // Använd 'userId' för att identifiera vilken användare som ska raderas.

        try {
            userService.deleteUser(userId); // Anropa din användarens tjänst för att radera användaren
            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete user");
        }
    }


}

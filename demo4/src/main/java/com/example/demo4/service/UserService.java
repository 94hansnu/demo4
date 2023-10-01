package com.example.demo4.service;

import com.example.demo4.payload.User;
import com.example.demo4.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        // Kontrollera om användaren finns
        Optional<User> existingUser = userRepository.findById(user.getId());
        if (existingUser.isPresent()) {
            // Uppdatera användaren om den finns
            return userRepository.save(user);
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    public void deleteUser(Long userId) {
        // Kontrollera om användaren finns
        Optional<User> existingUser = userRepository.findById(userId);
        if (existingUser.isPresent()) {
            // Ta bort användaren om den finns
            userRepository.deleteById(userId);
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    /*public List<User> getAllUsers() {
        return userRepository.findAll();
    }*/

    public User getUserById(Long userId) {
        // Hämta användaren om den finns
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}

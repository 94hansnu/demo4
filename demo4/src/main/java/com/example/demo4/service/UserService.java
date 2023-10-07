package com.example.demo4.service;
import com.example.demo4.payload.User;
import com.example.demo4.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

/**
 * Detta är en serviceklass som hanterar användaroperationer och interagerar med UserRepository.
 * Autowired-annotationen används för att injicera en instans av UserRepository i klassen.
 * Metoden addUser lägger till en användare i databasen.
 * Metoden updateUser uppdaterar en användare i databasen, kontrollerar om användaren finns och uppdaterar användaren
 * om den finns.
 * Metoden deleteUser raderar en användare från databasen, kontrollerar om användaren finns och tar bort användaren.
 * Metoden getUserById hämtar en användare från databasen om användaren finns baserat på användarens id
 */
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

        Optional<User> existingUser = userRepository.findById(user.getId());
        if (existingUser.isPresent()) {
            return userRepository.save(user);
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    public void deleteUser(Long userId) {

        Optional<User> existingUser = userRepository.findById(userId);
        if (existingUser.isPresent()) {
            userRepository.deleteById(userId);
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    public User getUserById(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}

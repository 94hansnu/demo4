package com.example.demo4;

import com.example.demo4.payload.User;
import com.example.demo4.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatabaseTest {

    @Autowired
    UserRepository userRepository;

    static User testUser;

    @BeforeEach
    void setUp() {
        System.out.println("Before Test");
    }

    @AfterEach
    void tearDown() {
        System.out.println("After Test");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Alla test avslutade!");
    }

    @Test
    @Order(1)
    void createUser() {
        //Skapa ett objekt av User med specifik data
        User user = new User();
        user.setFirstName("Hanadi");
        user.setLastName("Snunu");

        //Spara user till DB
        testUser = userRepository.save(user);

        assertNotNull(userRepository.findById(testUser.getId()).get().getFirstName());

        System.out.println(testUser.getId());
    }

    @Test
    @Order(2)
    void updateUser() {
        //Hämta User med id 1
        User fetchedUser = userRepository.findById(testUser.getId()).get();
        assertNotNull(fetchedUser);

        //Uppdatera värdet i fetchedUser
        fetchedUser.setFirstName("Jones");
        userRepository.save(fetchedUser);
        assertEquals("Jones", userRepository.findById(testUser.getId()).get().getFirstName());
    }

    @Test
    @Order(3)
    void removeUser() {
        //Kontrollera att user med ID 1 finns
        assertNotNull(userRepository.findById(testUser.getId()).get());

        //Ta bort user med ID 1 och kontroller att user är borta
        userRepository.deleteById(testUser.getId());
        assertTrue(userRepository.findById(testUser.getId()).isEmpty());
    }

    @Test
    @Order(4)
    void getAllUsers() {
        // Skapa några användare och spara dem i databasen
        User user1 = new User();
        user1.setFirstName("Kelly");
        user1.setLastName("Andersson");

        User user2 = new User();
        user2.setFirstName("Mohamad");
        user2.setLastName("Abou-Arab");

        userRepository.saveAll(List.of(user1, user2));

        // Hämta alla användare från databasen
        List<User> allUsers = userRepository.findAll();

        // Kontrollera att antalet hämtade användare är korrekt
        assertEquals(2, allUsers.size());

        // Rensa upp efter testet
        userRepository.deleteAll(List.of(user1, user2));
    }
    @Test
    @Order(5)
    void handleInvalidOperations() {
        // Skapa en användare och spara den i databasen
        User user1 = new User();
        user1.setFirstName("Hanadi");
        user1.setLastName("Snunu");

        userRepository.save(user1);

        // Försök att lägga till en användare med samma ID
        User user2 = new User();
        user2.setId(user1.getId());
        user2.setFirstName("Markus");
        user2.setLastName("Henriksson");

        // Försök att spara den andra användaren
        assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(user2));

        // Rensa upp efter testet
        userRepository.delete(user1);
    }


}
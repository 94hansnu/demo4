package com.example.demo4;
import com.example.demo4.payload.User;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class KafkaTest {

    private static User user;
    private static JSONObject myObj;

    @BeforeAll
    static void beforeAll() {
        user = new User();
        user.setFirstName("ff");
        user.setLastName("gg");
        user.setId(24L);

        myObj = new JSONObject();
        myObj.put("id", user.getId());
        myObj.put("firstName", user.getFirstName());
        myObj.put("lastName", user.getLastName());
    }

    @Test
    @Order(1)
    public void sendToWebAPITest() {
        //Anropa metod för att skicka data till webb-API
        String resp = Main.sendToWebAPI(myObj);

        //Jämföra response-värden
        assertEquals(resp, "");
    }

    @Test
    @Order(2)
    public void getDataFromKafkaTest() {
        //Anropa metod för att hämta Users
        ArrayList<User> users = Main.getDataFromKafka("javaJsonGuides");

        //Jämför att åtminstone en användare hämtades
        assertFalse(users.isEmpty(), "No users retrieved");

        //Jämför första användarens data med testanvändarens data
        User testUser = users.get(0);
        assertEquals( testUser.getFirstName() , user.getFirstName());
        assertEquals( testUser.getLastName() , user.getLastName());
        assertEquals( testUser.getId() , user.getId());
    }


}

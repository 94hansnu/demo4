package com.example.demo4;

import com.example.demo4.payload.User;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.simple.JSONObject;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws MalformedURLException, org.json.simple.parser.ParseException {
        System.out.println("Hello world!");

        userMenu();

    }

    public static void userMenu() {
        String userChoise = "";

        do {

            printMenu();

            Scanner scan = new Scanner(System.in);
            System.out.print("Gör ditt val: ");
            userChoise = scan.nextLine();

            //SwitchCase
            switch (userChoise) {
                case "1": {
                    userInputForKafka();
                    break;
                }
                case "2": {
                    getDataFromKafka("javaJsonGuides");
                    break;
                }
                case "3":{
                    addUser();
                    break;
                }
                case "4": {
                    updateUser();
                    break;
                }
                case "5": {
                    deleteUser();
                    break;
                }
                case "0": {
                    System.out.println("Du har valt att avsluta programmet.");
                    break;
                }
                default: {
                    System.out.println("Felaktig input. Försök igen");
                    break;
                }
            }

            if (!userChoise.equals("0")) {
                System.out.println("Press any key to continue");
                scan.nextLine();
            }

        } while (!userChoise.equals("0"));
    }

    public static void printMenu() {
        /* Writing up the meny */
        System.out.println("Gör dit val!");
        System.out.println("------------");
        System.out.println("1. Skriv data till Kafka Server");
        System.out.println("2. Hämta data från Kafka Server");
        System.out.println("3. Lägg till användare");
        System.out.println("4. Uppdatera användare");
        System.out.println("5. Radera användare");
        System.out.println("0. Avsluta");
    }

      public static void userInputForKafka() {
        User user = new User();

        //Logik flr att låta användaren mata in data//

        user.setId(11L);
        user.setFirstName("Cihan");
        user.setLastName("Vivera");

        JSONObject myObj = new JSONObject();
        myObj.put("id", user.getId());
        myObj.put("firstName", user.getFirstName());
        myObj.put("lastName", user.getLastName());



        //Skicka Payload till WebAPI via en Request
        sendToWebAPI(myObj);
    }

    public static String sendToWebAPI(JSONObject myObj) {
        String returnResp = "";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost("http://localhost:8080/api/v1/kafka/publish");

            // Skapa en JSON-förfrågningskropp
            String jsonPayload = myObj.toJSONString();
            StringEntity entity = new StringEntity(jsonPayload, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);

            // Skicka förfrågan och hantera svaret
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null) {
                    String responseString = EntityUtils.toString(responseEntity);
                    System.out.println("Svar från server: " + responseString);
                    returnResp = responseString;
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) { e.printStackTrace(); }
        return returnResp;
    }

    public static ArrayList<User> getDataFromKafka(String topicName) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "fetchingGroup");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put("spring.json.trusted.packages", "*");

        Consumer<String, User> consumer = new KafkaConsumer<>(props);

        consumer.assign(Collections.singletonList(new TopicPartition(topicName, 0)));

        //Gå till början av Topic
        consumer.seekToBeginning(consumer.assignment());

        //Create User list
        ArrayList<User> users = new ArrayList<User>();

        //WhileLoop osm hämtar i JSON format
        while (true) {
            ConsumerRecords<String, User> records = consumer.poll(Duration.ofMillis(100));
            if (records.isEmpty()) continue;
            for (ConsumerRecord<String, User> record : records) {
                users.add(record.value());
            }
            break;
        }

        for (User user : users) {
            System.out.println(user.getFirstName());
        }

        return users;
    }

    public static void addUser() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Ange användarens ID: ");
        Long userId = Long.parseLong(scanner.nextLine());

        System.out.print("Ange förnamn: ");
        String firstName = scanner.nextLine();

        System.out.print("Ange efternamn: ");
        String lastName = scanner.nextLine();

        User user = new User();
        user.setId(userId);
        user.setFirstName(firstName);
        user.setLastName(lastName);

        JSONObject jsonUser = new JSONObject();
        jsonUser.put("id", user.getId());
        jsonUser.put("firstName", user.getFirstName());
        jsonUser.put("lastName", user.getLastName());

        // Skicka användaren till webb-API för att lägga till
        sendToWebAPI("addUser", jsonUser);
    }

    public static void updateUser() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Ange användarens ID att uppdatera: ");
        Long userId = Long.parseLong(scanner.nextLine());

        System.out.print("Ange nytt förnamn: ");
        String newFirstName = scanner.nextLine();

        System.out.print("Ange nytt efternamn: ");
        String newLastName = scanner.nextLine();

        JSONObject jsonUpdate = new JSONObject();
        jsonUpdate.put("id", userId);
        jsonUpdate.put("firstName", newFirstName);
        jsonUpdate.put("lastName", newLastName);

        // Skicka uppdateringen till webb-API
        sendToWebAPI("updateUser", jsonUpdate);
    }

    public static void deleteUser() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Ange användarens ID att radera: ");
        Long userId = Long.parseLong(scanner.nextLine());
        // Konstruera URL för att radera användaren
        String apiUrl = "http://localhost:8080/api/v1/kafka/deleteUser/" + userId;

        // Använd sendDeleteRequest-metoden för att radera användaren
        sendDeleteRequest(apiUrl);
    }
    public static String sendDeleteRequest(String apiUrl) {
        String returnResp = "";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpDelete httpDelete = new HttpDelete(apiUrl);

            // Skicka förfrågan och hantera svaret
            try (CloseableHttpResponse response = httpClient.execute(httpDelete)) {
                int statusCode = response.getCode();
                if (statusCode == HttpStatus.SC_OK) {
                    System.out.println("Användare raderad framgångsrikt.");
                } else {
                    System.out.println("Misslyckades med att radera användaren. Statuskod: " + statusCode);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnResp;
    }

    public static String sendToWebAPI(String apiUrl, JSONObject myObj) {
        String returnResp = "";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost("http://localhost:8080/api/v1/kafka/" + apiUrl);

            if (myObj != null) {
                // Skapa en JSON-förfrågningskropp
                String jsonPayload = myObj.toJSONString();
                StringEntity entity = new StringEntity(jsonPayload, ContentType.APPLICATION_JSON);
                httpPost.setEntity(entity);
            }

            // Skicka förfrågan och hantera svaret
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null) {
                    String responseString = EntityUtils.toString(responseEntity);
                    System.out.println("Svar från server: " + responseString);
                    returnResp = responseString;
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnResp;
    }

}

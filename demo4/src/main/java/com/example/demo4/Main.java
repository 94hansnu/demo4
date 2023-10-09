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
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.Duration;
import java.util.*;

/**
 * Detta är huvudklassen som innehåller användargränssnittet och interaktion med Kafka och webb-API.
 * Metoden userMenu visar användarmenyn och switch-case för att hantera användarens val.
 * Metoden printMenu skriver ut användarmenyn.
 * Metoden sendToWebAPI skickar data till ett webb-API och returnerar svaret från webb-API
 * Metoden getDataFromKafka hämtar data från kafka och returnerar listan med användare.
 * Metoden addUser lägger till en användare i JSON-format och skickar det till webb-API
 * Metoden updateUser uppdaterar en användare i JSON-format och skickar det till webb-API
 * Metoden deleteUser raderar en användare och skickar data till webb-API
 */
public class Main {
    public static void main(String[] args) throws MalformedURLException, org.json.simple.parser.ParseException {

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
                   List<User> users = getDataFromKafka("javaJsonGuides");
                   displayUserInformation(users);
                    break;
                }
                case "2":{
                    addUser();
                    break;
                }
                case "3": {
                    updateUser();
                    break;
                }
                case "4": {
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
    public static void displayUserInformation(List<User> users) {
        System.out.println("-----------------------------");
        for (User user : users) {
            System.out.println("User ID: " + user.getId());
            System.out.println("Förnamn: " + user.getFirstName());
            System.out.println("Efternamn: " + user.getLastName());
            System.out.println("----------------------------");
        }
    }

    public static void printMenu() {

        System.out.println("Gör dit val!");
        System.out.println("------------");
        System.out.println("1. Hämta data från Kafka Server");
        System.out.println("2. Lägg till användare");
        System.out.println("3. Uppdatera användare");
        System.out.println("4. Radera användare");
        System.out.println("0. Avsluta");
    }


    public static String sendToWebAPI(JSONObject myObj) {
        String returnResp = "";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost("http://localhost:8080/api/v1/kafka/publish");

            String jsonPayload = myObj.toJSONString();
            StringEntity entity = new StringEntity(jsonPayload, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);

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

        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        Consumer<String, User> consumer = new KafkaConsumer<>(props);

        consumer.assign(Collections.singletonList(new TopicPartition(topicName, 0)));

        consumer.seekToBeginning(consumer.assignment());

        ArrayList<User> users = new ArrayList<>();

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

        sendToWebAPI(jsonUser);
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

        sendToWebAPI(jsonUpdate);
    }

    public static void deleteUser() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Ange användarens ID att radera: ");
        Long userId = Long.parseLong(scanner.nextLine());
        String apiUrl = "http://localhost:8080/api/v1/kafka/deleteUser/" + userId;
        sendDeleteRequest(apiUrl);
    }
    public static String sendDeleteRequest(String apiUrl) {
        String returnResp = "";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpDelete httpDelete = new HttpDelete(apiUrl);

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

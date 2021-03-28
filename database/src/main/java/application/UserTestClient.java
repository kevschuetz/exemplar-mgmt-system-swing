package application;

import java.net.http.HttpClient;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;

public class UserTestClient {
    public static void main(String[] args) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/user"))
                .build();

        /**
         * Test for getUsers()
         */
        System.out.println("getUsers()");
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


       ObjectMapper mapper = new ObjectMapper();
       Users[] asArray = mapper.readValue(response.body(), Users[].class);
       for(int i = 0; i < asArray.length; i++)
           System.out.println(asArray[i].toString());



        /**
         *Test createUser()
         *
         */
        System.out.println("createUser()");
        mapper = new ObjectMapper();
        Users testUser = new Users();
        testUser.setUsername("Kevin");
        testUser.setIsContributor(0);
        String json = mapper.writeValueAsString(testUser);
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/user"))
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);
        System.out.println(response.body());

        /**
         *Test getUser()
         *
         */
        System.out.println("getUser()");
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/user/momosuke"))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());

        /**
         *Test deleteUser()
         *
         */
        System.out.println("deleteUser()");
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/user/Kevin"))
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);
    }

}

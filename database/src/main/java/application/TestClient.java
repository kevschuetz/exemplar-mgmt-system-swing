package application;

import java.net.http.HttpClient;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestClient {
    public static void main(String[] args) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/user"))
                .build();

        /**
         * Test for getUsers()
         */

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


       ObjectMapper mapper = new ObjectMapper();
       Users[] asArray = mapper.readValue(response.body(), Users[].class);
       for(int i = 0; i < asArray.length; i++)
           System.out.println(asArray[i].toString());

        /**
         *Test findUser()
         *
         */

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/user/momosuke"))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());




    }

}

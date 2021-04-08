package main;

import java.net.http.HttpClient;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;

public class UserTestClient {
    public static void main(String[] args) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        String url = "http://database.northeurope.azurecontainer.io:8080";
        url = "http://localhost:8080";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/user"))
                .build();

        /**
         * Test for getUsers()
         */
        System.out.println("getUsers()");
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


       ObjectMapper mapper = new ObjectMapper();
       User[] asArray = mapper.readValue(response.body(), User[].class);
       for(int i = 0; i < asArray.length; i++)
           System.out.println(asArray[i].toString());



        /**
         *Test createUser()
         *
         */
        System.out.println("createUser()");
        mapper = new ObjectMapper();
        User testUser = new User();
        testUser.setUsername("Franz");
        testUser.setIsContributor(0);
        testUser.setFullName("kevin");
        testUser.setPassword("password");
        String json = mapper.writeValueAsString(testUser);
        request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/user"))
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
                .uri(URI.create(url+"/user/kevin"))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());

        /**
         *Test deleteUser()
         *
         */
        System.out.println("deleteUser()");
        request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/user/Kevin"))
                .DELETE()
                .build();
        // response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);



        /**
         *Test addExemplar
         *
         */
        Exemplar exemplar = new Exemplar();
        ArrayList<User> contributors = new ArrayList<>();
        contributors.add(new User("franz", "aigner", "password", 1));
        exemplar.setContributors(contributors);
        exemplar.setName("exemplar1");
        ArrayList<Label> labels = new ArrayList<>();
        //labels.add(new Label("java"));
        exemplar.setLabels(labels);
        System.out.println("addExemplar()");
        json= mapper.writeValueAsString(exemplar);
        request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/exemplar"))
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);
    }

}

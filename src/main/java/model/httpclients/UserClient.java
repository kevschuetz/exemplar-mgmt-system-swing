package model.httpclients;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.entities.Users;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;



public class UserClient extends Client<Users> {
    private final HttpClient client;
    private final String URL;
    private final ObjectMapper mapper;

    private HttpRequest request;
    private HttpResponse<String> response;

    public UserClient(){
        client = HttpClient.newHttpClient();
        URL = HttpConstants.URL + "/user";
        mapper = new ObjectMapper();
    };



    @Override
    public void add(Users value) throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(value)))
                .build();
        response= client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public void delete(String id) throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .uri(URI.create(URL+"/"+id))
                .DELETE()
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public Users[] getAll() throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println( "body: " + response.body());

        Users[] asArray = mapper.readValue(response.body(), Users[].class);
        return asArray;
    }

    @Override
    public Users get(String id) throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .uri(URI.create(URL+"/kevin"))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Users user = mapper.readValue(response.body(), Users.class);
        return user;
    }
}

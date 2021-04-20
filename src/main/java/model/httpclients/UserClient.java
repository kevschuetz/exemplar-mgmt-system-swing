package model.httpclients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.entities.User;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedList;
import java.util.List;


public class UserClient extends Client<User> {
    private final HttpClient client;
    private final String URL;
    private final ObjectMapper mapper;

    private HttpRequest request;
    private HttpResponse<String> response;

    public UserClient(){
        client = HttpClient.newHttpClient();
        URL = HttpConstants.URL + "/users";
        mapper = new ObjectMapper();
    };



    @Override
    public User add(User value) throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(value)))
                .build();
        response= client.send(request, HttpResponse.BodyHandlers.ofString());
        try{
            User addedUser =  mapper.readValue(response.body(), User.class);
            return addedUser;
        }catch (Exception e){
            return null;
        }
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
    public List<User> getAll() throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        try{
            List<User> users = mapper.readValue(response.body(), new TypeReference<List<User>>(){});
            return users;
        }catch(Exception e){
            //e.printStackTrace();
            return new LinkedList<User>();
        }
    }

    @Override
    public User get(String id) throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .uri(URI.create(URL+"/"+id))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        try {
            User user = mapper.readValue(response.body(), User.class);
            return user;
        }catch(Exception e){
            //e.printStackTrace();
            return null;
        }
    }

    @Override
    public User update(String id, User value) throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .uri(URI.create(URL+"/"+id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(value)))
                .build();
        response= client.send(request, HttpResponse.BodyHandlers.ofString());
        try{
            User updatedUser =  mapper.readValue(response.body(), User.class);
            return updatedUser;
        }catch (Exception e){
            return null;
        }
    }
}

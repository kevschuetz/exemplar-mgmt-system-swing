package model.httpclients;

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
    }



    @Override
    public User add(User value) throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(value)))
                .build();
        response= client.send(request, HttpResponse.BodyHandlers.ofString());
        try{
           return mapper.readValue(response.body(), User.class);
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
            return mapper.readValue(response.body(), new TypeReference<List<User>>(){});
        }catch(Exception e){
            e.printStackTrace();
            return new LinkedList<>();
        }
    }

    @Override
    public User get(String id) throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .uri(URI.create(URL+"/"+id))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        try {
           return mapper.readValue(response.body(), User.class);
        }catch(Exception e){
            e.printStackTrace();
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
            return mapper.readValue(response.body(), User.class);
        }catch (Exception e){
            return null;
        }
    }


    public List<User> searchUsers(String value) {
        try{
        request = HttpRequest.newBuilder()
                .uri(URI.create(URL+"/search?value="+value))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

       return mapper.readValue(response.body(), new TypeReference<List<User>>(){});
        }catch(Exception e){
            e.printStackTrace();
            return new LinkedList<>();
        }
    }
}

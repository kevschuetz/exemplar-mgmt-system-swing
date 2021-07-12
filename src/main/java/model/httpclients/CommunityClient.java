package model.httpclients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.entities.Community;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedList;
import java.util.List;

/**
 * Used to interact with the Community entity
 */
public class CommunityClient extends Client<Community>{
    private final HttpClient client;
    private final String url;
    private final ObjectMapper mapper;
    private HttpRequest request;
    private HttpResponse<String> response;
    public CommunityClient(){
        client = HttpClient.newHttpClient();
        url = HttpConstants.URL + "/communities";
        mapper = new ObjectMapper();
    }

    @Override
    public Community add(Community value) throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(value)))
                .build();
        response= client.send(request, HttpResponse.BodyHandlers.ofString());
        try{
            return mapper.readValue(response.body(), Community.class);

        }catch (Exception e){
            return null;
        }
    }

    @Override
    public void delete(String id) throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .uri(URI.create(url +"/"+id))
                .DELETE()
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public List<Community> getAll() throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        try{
           return mapper.readValue(response.body(), new TypeReference<List<Community>>(){});

        }catch(Exception e){
            e.printStackTrace();
            return new LinkedList<>();
        }
    }

    @Override
    public Community get(String id) throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .uri(URI.create(url +"/"+id))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        try {
            return mapper.readValue(response.body(), Community.class);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Community update(String id, Community value) {
        try {
            request = HttpRequest.newBuilder()
                    .uri(URI.create(url +"/"+id))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(value)))
                    .build();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        try {
            response= client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        Community updated = null;
        try {
            updated = mapper.readValue(response.body(), Community.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return updated;
    }

    public List<Community> getCommunitiesForUser(String username){
        try{
        request = HttpRequest.newBuilder()
                .uri(URI.create(url +"/member?member="+username))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

          return mapper.readValue(response.body(), new TypeReference<List<Community>>(){});

        }catch(Exception e){
            e.printStackTrace();
            return new LinkedList<>();
        }
    }

    public List<Community> searchCommunities(String value){
        try{
            request = HttpRequest.newBuilder()
                    .uri(URI.create(url +"/search?value="+value))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return mapper.readValue(response.body(), new TypeReference<List<Community>>(){});
        } catch(InterruptedException | IOException e){
            e.printStackTrace();
            return new LinkedList<>();
        }
    }


}

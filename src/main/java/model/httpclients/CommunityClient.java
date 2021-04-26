package model.httpclients;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.entities.Community;
import model.entities.Exemplar;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedList;
import java.util.List;

public class CommunityClient extends Client<Community>{
    private final HttpClient client;
    private final String URL;
    private final ObjectMapper mapper;

    private HttpRequest request;
    private HttpResponse<String> response;
    public CommunityClient(){
        client = HttpClient.newHttpClient();
        URL = HttpConstants.URL + "/communities";
        mapper = new ObjectMapper();
    };

    @Override
    public Community add(Community value) throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(value)))
                .build();
        response= client.send(request, HttpResponse.BodyHandlers.ofString());
        try{
            Community added =  mapper.readValue(response.body(), Community.class);
            return added;
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
    public List<Community> getAll() throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        try{
            List<Community> all = mapper.readValue(response.body(), new TypeReference<List<Community>>(){});
            return all;
        }catch(Exception e){
            //e.printStackTrace();
            return new LinkedList<Community>();
        }
    }

    @Override
    public Community get(String id) throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .uri(URI.create(URL+"/"+id))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        try {
            Community entity = mapper.readValue(response.body(), Community.class);
            return entity;
        }catch(Exception e){
            //e.printStackTrace();
            return null;
        }
    }

    @Override
    public Community update(String id, Community value) throws IOException, InterruptedException {
        try{
            request = HttpRequest.newBuilder()
                    .uri(URI.create(URL+"/"+id))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(value)))
                    .build();
            response= client.send(request, HttpResponse.BodyHandlers.ofString());
            Community updated =  mapper.readValue(response.body(), Community.class);
            return updated;
        }catch (Exception e){
            return null;
        }
    }

    public List<Community> getCommunitiesForUser(String username){
        try{
        request = HttpRequest.newBuilder()
                .uri(URI.create(URL+"/member?member="+username))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

            List<Community> all = mapper.readValue(response.body(), new TypeReference<List<Community>>(){});
            return all;
        }catch(Exception e){
            //e.printStackTrace();
            return new LinkedList<Community>();
        }


    }


}

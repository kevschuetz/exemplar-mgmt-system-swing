package model.httpclients;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.entities.Label;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class LabelClient extends Client<Label>{
    private final HttpClient client;
    private final String URL;
    private final ObjectMapper mapper;
    private HttpRequest request;
    private HttpResponse<String> response;
    public LabelClient(){
        client = HttpClient.newHttpClient();
        URL = HttpConstants.URL + "/labels";
        mapper = new ObjectMapper();
    }

    @Override
    public Label add(Label value) {
        try{
        request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(value)))
                .build();
        response= client.send(request, HttpResponse.BodyHandlers.ofString());

           return mapper.readValue(response.body(), Label.class);
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public void delete(String id) throws IOException, InterruptedException {
        //not needed
    }

    @Override
    public List<Label> getAll() throws IOException, InterruptedException {
        return new ArrayList<>();
    }

    @Override
    public Label get(String id) throws IOException, InterruptedException {
        return null;
    }

    @Override
    public Label update(String id, Label value) throws IOException, InterruptedException {
        return null;
    }
}

package model.httpclients;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.entities.Label;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class LabelClient {
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

}

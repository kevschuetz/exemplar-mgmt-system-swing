package model.httpclients;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.entities.Exemplar;
import model.entities.Users;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ExemplarClient extends Client<Exemplar>{
    private final HttpClient client;
    private final String URL;
    private final ObjectMapper mapper;

    private HttpRequest request;
    private HttpResponse<String> response;

    public ExemplarClient(){
        client = HttpClient.newHttpClient();
        URL = HttpConstants.URL + "/exemplar";
        mapper = new ObjectMapper();
    };

    @Override
    public void add(Exemplar value) throws IOException, InterruptedException {

    }

    @Override
    public void delete(String id) throws IOException, InterruptedException {

    }

    @Override
    public Exemplar[] getAll() throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());


        Exemplar[] asArray = mapper.readValue(response.body(), Exemplar[].class);
        return asArray;
    }

    @Override
    public Exemplar get(String id) throws IOException, InterruptedException {
        return null;
    }
}

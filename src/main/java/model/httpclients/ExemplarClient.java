package model.httpclients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.entities.Exemplar;
import model.entities.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedList;
import java.util.List;

public class ExemplarClient extends Client<Exemplar>{
    private final HttpClient client;
    private final String URL;
    private final ObjectMapper mapper;

    private HttpRequest request;
    private HttpResponse<String> response;

    public ExemplarClient(){
        client = HttpClient.newHttpClient();
        URL = HttpConstants.URL + "/exemplars";
        mapper = new ObjectMapper();
    };

    @Override
    public Exemplar add(Exemplar value) throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(value)))
                .build();
        response= client.send(request, HttpResponse.BodyHandlers.ofString());
        try{
            Exemplar addedExemplar =  mapper.readValue(response.body(), Exemplar.class);
            return addedExemplar;
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
    public List<Exemplar> getAll() throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        try{
            List<Exemplar> exemplars = mapper.readValue(response.body(), new TypeReference<List<Exemplar>>(){});
            return exemplars;
        }catch(Exception e){
            //e.printStackTrace();
            return new LinkedList<Exemplar>();
        }
    }

    @Override
    public Exemplar get(String id) throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .uri(URI.create(URL+"/"+id))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        try {
            Exemplar exemplar = mapper.readValue(response.body(), Exemplar.class);
            return exemplar;
        }catch(Exception e){
            //e.printStackTrace();
            return null;
        }
    }

    @Override
    public Exemplar update(String id, Exemplar value)  {
        try{
        request = HttpRequest.newBuilder()
                .uri(URI.create(URL+"/"+id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(value)))
                .build();
        response= client.send(request, HttpResponse.BodyHandlers.ofString());
        Exemplar updatedExemplar =  mapper.readValue(response.body(), Exemplar.class);
        return updatedExemplar;
        }catch (Exception e){
            return null;
        }
    }

    public List<Exemplar> getExemplarForCreator(String creator){
        try{
        request = HttpRequest.newBuilder()
                .uri(URI.create(URL+"/creator?creator="+creator))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<Exemplar> exemplars = mapper.readValue(response.body(), new TypeReference<List<Exemplar>>(){});
            return exemplars;
        }catch(Exception e){
            //e.printStackTrace();
            return new LinkedList<Exemplar>();
        }
    }
}

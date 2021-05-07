package model.httpclients;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.entities.Community;
import model.entities.Rating;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedList;
import java.util.List;

public class RatingClient extends Client<Rating> {
    private final HttpClient client;
    private final String URL;
    private final ObjectMapper mapper;

    private HttpRequest request;
    private HttpResponse<String> response;
    public RatingClient(){
        client = HttpClient.newHttpClient();
        URL = HttpConstants.URL + "/ratings";
        mapper = new ObjectMapper();
    };

    @Override
    public Rating add(Rating value) throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(value)))
                .build();
        response= client.send(request, HttpResponse.BodyHandlers.ofString());
        try{
            Rating added =  mapper.readValue(response.body(), Rating.class);
            return added;
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public void delete(String id) throws IOException, InterruptedException {
        String[] array = id.split(",");
        if (array.length != 2) return;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL+"/"+array[0]+"?username="+array[1]))
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public List<Rating> getAll() {
        try{
        request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

            List<Rating> all = mapper.readValue(response.body(), new TypeReference<List<Rating>>(){});
            return all;
        }catch(Exception e){
            //e.printStackTrace();
            return new LinkedList<Rating>();
        }
    }

    @Override
    public Rating get(String id) throws IOException, InterruptedException {
        String[] array = id.split(",");
        if (array.length != 2) return null;
        request = HttpRequest.newBuilder()
                .uri(URI.create(URL+"/"+array[0]+"?username="+array[1]))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        try {
            Rating entity = mapper.readValue(response.body(), Rating.class);
            return entity;
        }catch(Exception e){
            //e.printStackTrace();
            return null;
        }
    }

    @Override
    public Rating update(String exemplarId, Rating value) throws IOException, InterruptedException {
       return add(value);
    }

    public List<Rating> getRatingsForExemplar(String exemplarname){
        try{
        request = HttpRequest.newBuilder()
                .uri(URI.create(URL+"/forexemplar?exemplarname="+exemplarname))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Rating> ratings = mapper.readValue(response.body(), new TypeReference<List<Rating>>(){});
        return ratings;
        }catch(Exception e){
            //e.printStackTrace();
            return new LinkedList<Rating>();
        }
    }

    public double getAvgRatingForExemplar(String id) {
        try {
        request = HttpRequest.newBuilder()
                .uri(URI.create(URL+"/average/"+id))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Double avg = mapper.readValue(response.body(), Double.class);
        return avg;
        }catch(Exception e){
            //e.printStackTrace();
            return 0;
        }
    }


}

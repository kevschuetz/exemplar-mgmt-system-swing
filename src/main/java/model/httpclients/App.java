package model.httpclients;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.entities.Exemplar;
import model.entities.Rating;
import model.entities.RatingPK;
import model.entities.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class App {
    public static void main(String[] args) throws IOException, InterruptedException {
         RatingClient client;
         Rating testEntity;
        boolean errorOccured;

        client = new RatingClient();
        testEntity = new Rating();
        RatingPK key = new RatingPK();
        User user = new User("testUserForRating", "fullName", "password", 1);
        Exemplar exemplar = new Exemplar();
        exemplar.setName("testExemplarForRating");
        new ExemplarClient().add(exemplar);
        new UserClient().add(user);
        key.setExemplar(exemplar);
        key.setUser(user);
        testEntity.setKey(key);
        testEntity.setRating(5.0);

        client.add(testEntity);
        //client.delete(testEntity.getKey().toString());




        HttpClient httpclient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/ratings/"+testEntity.getKey().getExemplar().getName()+"?username="+testEntity.getKey().getUser().getUsername()))
                .DELETE()
                .build();
        HttpResponse<String> response = httpclient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());


        Rating rating = client.get(testEntity.getKey().toString());

    }
}

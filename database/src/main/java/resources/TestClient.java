package resources;

import java.net.http.HttpClient;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;

public class TestClient {
    public static void main(String[] args) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        String url = "http://database.northeurope.azurecontainer.io:8080";
        url = "http://localhost:8080";
        /**
         *Test createUser()
         *
         */
        System.out.println("createUser()");
        mapper = new ObjectMapper();
        User testUser = new User();
        testUser.setUsername("kevin");
        testUser.setIsContributor(0);
        testUser.setFullName("kevin");
        testUser.setPassword("password");
        String json = mapper.writeValueAsString(testUser);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/user"))
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);
        System.out.println(response.body());



        /**
         * Test for getUsers()
         */
         request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/user"))
                .build();
        System.out.println("getUsers()");
        response = client.send(request, HttpResponse.BodyHandlers.ofString());



       User[] asArray = mapper.readValue(response.body(), User[].class);
       for(int i = 0; i < asArray.length; i++)
           System.out.println(asArray[i].toString());

        /**
         *Test getUser()
         *
         */
        System.out.println("getUser()");
        request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/user/kevin"))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());




        /**
         *Test addLabel
         *
         */
        System.out.println("addLabel()");
        mapper = new ObjectMapper();
        Label label = new Label("java");
        json = mapper.writeValueAsString(label);
        request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/label"))
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);
        System.out.println(response.body());

        /**
         * Test for getLabels()
         */
        request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/label"))
                .build();
        System.out.println("getLabels()");
        response = client.send(request, HttpResponse.BodyHandlers.ofString());



        Label[] labelArray = mapper.readValue(response.body(), Label[].class);
        for(int i = 0; i < labelArray.length; i++)
            System.out.println(labelArray[i].toString());


        /**
         *Test getLabel()
         *
         */
        System.out.println("getLabel()");
        request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/user/kevin"))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());


        /**
         *Test addExemplar
         *
         */




        Exemplar exemplar = new Exemplar();


        ArrayList<User> contributors = new ArrayList<>();
        contributors.add(testUser);
        exemplar.setContributors(contributors);
        exemplar.setName("exemplar1");
        ArrayList<Label> labels = new ArrayList<>();
        Label label2 = new Label("java");
        labels.add(label2);
        exemplar.setLabels(labels);
        System.out.println("addExemplar()");
        json= mapper.writeValueAsString(exemplar);
        request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/exemplar"))
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);

        /**
         * Test for getExemplars()
         */
        request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/exemplar"))
                .build();
        System.out.println("getExemplars()");
        response = client.send(request, HttpResponse.BodyHandlers.ofString());



        Exemplar[] exemplarArray = mapper.readValue(response.body(), Exemplar[].class);
        for(int i = 0; i < exemplarArray.length; i++)
            System.out.println(exemplarArray[i].toString());

        /**
         *Test getExemplar()
         *
         */
        System.out.println("getExemplar()");
        request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/exemplar/exemplar1"))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());


        /**
         *Test addCommunity
         *
         */
        Community community = new Community();
        ArrayList<User> members = new ArrayList<>();
        members.add(testUser);
        community.setUsers(members);
        community.setName("community1");
        ArrayList<Exemplar> exemplars = new ArrayList<>();
        exemplars.add(exemplar);
        community.setExemplars(exemplars);
        System.out.println("addCommunity()");
        json= mapper.writeValueAsString(community);
        request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/community"))
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);

        /**
         * Test for getCommunities()
         */
        request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/community"))
                .build();
        System.out.println("getCommunities()");
        response = client.send(request, HttpResponse.BodyHandlers.ofString());



        Community[] communityarray = mapper.readValue(response.body(), Community[].class);
        for(int i = 0; i < communityarray.length; i++)
            System.out.println(communityarray[i].toString());
        /**
         *Test getCommunity()
         *
         */
        System.out.println("getCommunity()");
        request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/community/community1"))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());


        /**
         *Test addRating
         *
         */
        // Rating creation--/
        Rating rating = new Rating();
        rating.setRating(5);
        RatingPK ratingPK = new RatingPK();
        ratingPK.setUser(testUser);
        ratingPK.setExemplar(exemplar);
        rating.setKey(ratingPK);


        System.out.println("addRating()");
        json= mapper.writeValueAsString(rating);
        request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/rating"))
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);

        /**
         * Test for getRatings()
         */
        request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/rating"))
                .build();
        System.out.println("getRatings()");
        response = client.send(request, HttpResponse.BodyHandlers.ofString());



        Rating[] ratingA = mapper.readValue(response.body(), Rating[].class);
        for(int i = 0; i < ratingA.length; i++)
            System.out.println(ratingA[i].toString());

        /**
         *Test getRating()
         *
         */
        System.out.println("getRating()");
        request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/rating?username=kevin&&exemplarname=exemplar1"))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());




        /**
         *Test deleteUser()
         *
         */
        System.out.println("deleteUser()");
        request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/user/Kevin"))
                .DELETE()
                .build();
        // response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);
    }

}

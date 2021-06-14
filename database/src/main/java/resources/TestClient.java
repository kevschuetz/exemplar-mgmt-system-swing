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
                .uri(URI.create(url+"/users"))
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);
        System.out.println(response.body());



        /**
         * Test for getUsers()
         */
         request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/users"))
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
                .uri(URI.create(url+"/users/kevin"))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        User testUser2 = mapper.readValue(response.body(), User.class);

        /**
         *Test getUser()
         *
        */
        System.out.println("searchusers()");
        request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/users/search?value=ad"))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());


        //fetch testUser and change fullname
        testUser2.setFullName("Mimsi");
        //add again
        System.out.println("adding fetched user again: ");
         json = mapper.writeValueAsString(testUser2);
        request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/users"))
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);
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
                .uri(URI.create(url+"/labels"))
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);
        System.out.println(response.body());

        /**
         * Test for getLabels()
         */
        request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/labels"))
                .build();
        System.out.println("getLabels()");
        response = client.send(request, HttpResponse.BodyHandlers.ofString());



        Label[] labelArray = mapper.readValue(response.body(), Label[].class);
        for(int i = 0; i < labelArray.length; i++)
            System.out.println(labelArray[i].getValue());


        /**
         *Test getLabel()
         *
         */
        System.out.println("getLabel()");
        request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/labels/java"))
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
                .uri(URI.create(url+"/exemplars"))
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);

        /**
         * Test for updateExemplar()
         */
        String oldName = exemplar.getName();
        exemplar.setName("newExemplarName");
        json = mapper.writeValueAsString(exemplar);
        System.out.println("updateExemplar()");
        request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/exemplars/"+oldName))
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);

        /**
         * Test for getExemplars()
         */
        request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/exemplars"))
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
                .uri(URI.create(url+"/exemplars/newExemplarName"))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());

     /**
      *Test getExemplarForCreator()
      *
      */
     String creator = "admin";
     System.out.println("getExemplarForCreator()");
     request = HttpRequest.newBuilder()
             .uri(URI.create(url+"/exemplars/creator?creator="+creator))
             .build();

     response = client.send(request, HttpResponse.BodyHandlers.ofString());
     System.out.println(response.body());

        /**
         *Test getExemplarForContributor()
         *
         */
        String contributor = creator;
        System.out.println("getExemplarForContributor()");
        request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/exemplars/contributor?contributor="+creator))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());

        /**
         *Test ExemplarForUser()
         *
         */
        String user = creator;
        System.out.println("getExemplarForUser()");
        request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/exemplars/user?user="+user))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());


        /**
         *Test SearchExemplars()
         *
         */
        String value = "exempl";
        System.out.println("searchExemplars");
        request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/exemplars/search?value="+value))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        System.out.println(response);
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
                .uri(URI.create(url+"/communities"))
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);

        /**
         * Test for updateCommunity (assertion= only one community by get)
         */
        String oldCommunityName = community.getName();
        community.setName("newName");
        json= mapper.writeValueAsString(community);

        request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/communities/"+oldCommunityName))
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);



        /**
         * Test for getCommunities()
         */
        request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/communities"))
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
                .uri(URI.create(url+"/communities/newName"))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());

        /**
         *Test getCommunity()
         *
         */
        System.out.println("getCommunitiesForUser()");
        request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/communities/member?member=kevin"))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());

        /**
         *Test getCommunity()
         *
         */
        System.out.println("searchCommunity()");
        request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/communities/search?value=c"))
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
                .uri(URI.create(url+"/ratings"))
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);

        /**
         * Test for getRatings()
         */
        request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/ratings"))
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
                .uri(URI.create(url+"/ratings/newExemplarName?username=kevin"))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());

        /**
         *Test getRatingforExemplar()
         *
         */
        System.out.println("getRatingForExemplar()");
        request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/ratings/forexemplar?exemplarname=exemplar1"))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());

        /**
         *Test getRatingforExemplar()
         *
         */
        System.out.println("getAvgRatingForExemplar()");
        request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/ratings/average/exemplar1"))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());


        /**
         *Test deleteUser()
         *
         */
        System.out.println("deleteUser()");
        request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/users/Kevin"))
                .DELETE()
                .build();
        // response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);
    }

}

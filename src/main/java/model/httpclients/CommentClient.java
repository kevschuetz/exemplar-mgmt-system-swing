package model.httpclients;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.entities.Comment;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedList;
import java.util.List;

/**
 * Used to interact with the Commment entity
 */
public class CommentClient extends Client<Comment>{
    private final HttpClient client;
    private final String url;
    private final ObjectMapper mapper;

    private HttpRequest request;
    private HttpResponse<String> response;

    public CommentClient(){
        client = HttpClient.newHttpClient();
        url = HttpConstants.URL + "/comments";
        mapper = new ObjectMapper();
    }

    @Override
    public Comment add(Comment value) throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(value)))
                .build();
        response= client.send(request, HttpResponse.BodyHandlers.ofString());
        try{
            return mapper.readValue(response.body(), Comment.class);
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
    public List<Comment> getAll() throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        try{
         return mapper.readValue(response.body(), new TypeReference<List<Comment>>(){});
        }catch(Exception e){
            e.printStackTrace();
            return new LinkedList<>();
        }
    }

    @Override
    public Comment get(String id) throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .uri(URI.create(url +"/"+id))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        try {
            return mapper.readValue(response.body(), Comment.class);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Comment update(String id, Comment value)  {
        try{
            request = HttpRequest.newBuilder()
                    .uri(URI.create(url +"/"+id))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(value)))
                    .build();
            response= client.send(request, HttpResponse.BodyHandlers.ofString());
            return mapper.readValue(response.body(), Comment.class);
        }catch (Exception e){
            return null;
        }
    }

    public List<Comment>findCommentsForExemplar(String exemplarname){
        try{
            request = HttpRequest.newBuilder()
                    .uri(URI.create(url +"/forexemplar?exemplarname="+exemplarname))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return mapper.readValue(response.body(), new TypeReference<List<Comment>>(){});
        }catch(Exception e){
            e.printStackTrace();
            return new LinkedList<>();
        }
    }


}

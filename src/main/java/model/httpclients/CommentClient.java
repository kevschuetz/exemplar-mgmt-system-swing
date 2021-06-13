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

public class CommentClient extends Client<Comment>{
    private final HttpClient client;
    private final String URL;
    private final ObjectMapper mapper;

    private HttpRequest request;
    private HttpResponse<String> response;

    public CommentClient(){
        client = HttpClient.newHttpClient();
        URL = HttpConstants.URL + "/comments";
        mapper = new ObjectMapper();
    };

    @Override
    public Comment add(Comment value) throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(value)))
                .build();
        response= client.send(request, HttpResponse.BodyHandlers.ofString());
        try{
            Comment addedComment =  mapper.readValue(response.body(), Comment.class);
            return addedComment;
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
    public List<Comment> getAll() throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        try{
            List<Comment> comments = mapper.readValue(response.body(), new TypeReference<List<Comment>>(){});
            return comments;
        }catch(Exception e){
            //e.printStackTrace();
            return new LinkedList<Comment>();
        }
    }

    @Override
    public Comment get(String id) throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .uri(URI.create(URL+"/"+id))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        try {
            Comment comment = mapper.readValue(response.body(), Comment.class);
            return comment;
        }catch(Exception e){
            //e.printStackTrace();
            return null;
        }
    }

    @Override
    public Comment update(String id, Comment value)  {
        try{
            request = HttpRequest.newBuilder()
                    .uri(URI.create(URL+"/"+id))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(value)))
                    .build();
            response= client.send(request, HttpResponse.BodyHandlers.ofString());
            Comment updatedComment =  mapper.readValue(response.body(), Comment.class);
            return updatedComment;
        }catch (Exception e){
            return null;
        }
    }

}

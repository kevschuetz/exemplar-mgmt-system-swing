package model.httpclients;


import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public abstract class Client<T> {
    private  HttpClient client;
    private  String URL;
    private  ObjectMapper mapper;

    private  HttpRequest request;
    private  HttpResponse<String> response;

    public abstract T add(T value) throws IOException, InterruptedException;
    public abstract void delete (String id) throws IOException, InterruptedException;
    public abstract List<T> getAll() throws IOException, InterruptedException;
    public abstract T get(String id) throws IOException, InterruptedException;
    public abstract T update(String id, T value) throws IOException, InterruptedException;

}

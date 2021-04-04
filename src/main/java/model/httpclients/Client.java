package model.httpclients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public abstract class Client<T> {
    private  HttpClient client;
    private  String URL;
    private  ObjectMapper mapper;

    private  HttpRequest request;
    private  HttpResponse<String> response;

    public abstract void add(T value) throws IOException, InterruptedException;
    public abstract void delete (String id) throws IOException, InterruptedException;
    public abstract T[] getAll() throws IOException, InterruptedException;
    public abstract T get(String id) throws IOException, InterruptedException;
    //public void update (T value, String attribute, String updatedValue);
}

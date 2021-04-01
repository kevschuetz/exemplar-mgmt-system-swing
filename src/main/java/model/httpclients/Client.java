package model.httpclients;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

public interface Client<T> {
    public void add(T value) throws IOException, InterruptedException;
    public void delete (String id) throws IOException, InterruptedException;
    public T[] getAll() throws IOException, InterruptedException;
    public T get(String id) throws IOException, InterruptedException;
    //public void update (T value, String attribute, String updatedValue);
}

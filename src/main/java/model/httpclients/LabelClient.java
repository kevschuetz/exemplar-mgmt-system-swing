package model.httpclients;

import model.entities.Label;

import java.io.IOException;
import java.util.List;

public class LabelClient extends Client<Label>{
    @Override
    public Label add(Label value) throws IOException, InterruptedException {
        return null;
    }

    @Override
    public void delete(String id) throws IOException, InterruptedException {

    }

    @Override
    public List<Label> getAll() throws IOException, InterruptedException {
        return null;
    }

    @Override
    public Label get(String id) throws IOException, InterruptedException {
        return null;
    }

    @Override
    public Label update(String id, Label value) throws IOException, InterruptedException {
        return null;
    }
}

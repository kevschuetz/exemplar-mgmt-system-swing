package main;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Label {
    @Id
    private String value;

    public Label(){

    }

    public Label(String name){
        this.value = name;
    }
}

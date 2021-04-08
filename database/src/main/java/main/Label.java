package main;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Label {
    @Id
    private String name;
}

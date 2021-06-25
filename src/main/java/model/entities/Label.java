package model.entities;

import java.util.Objects;

public class Label {
    private String value;

    public Label(){
        //empty constructor
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Label)) return false;
        Label label = (Label) o;
        return Objects.equals(getValue(), label.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}

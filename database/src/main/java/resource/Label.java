package resource;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Label {
    @Id
    private String value;

    public Label(){
    }

    public Label(String name){
        this.value = name;
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

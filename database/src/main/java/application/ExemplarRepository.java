package application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface ExemplarRepository extends JpaRepository<Exemplar, String> {
}

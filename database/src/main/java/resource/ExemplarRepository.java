package resource;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExemplarRepository extends JpaRepository<Exemplar, String> {



    @Query(value = "Select * from exemplar e where e.creator_username = ?1",
        nativeQuery = true)
    List<Exemplar> findExemplarsForCreator(String creator);
}

package resourcea;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, RatingPK> {


    @Query(value="SELECT * FROM rating where exemplar_name = ?1", nativeQuery = true)
    List<Rating> findRatingsForExemplar(String exemplar_name);
}

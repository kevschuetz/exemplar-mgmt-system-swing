package resources;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, RatingPK> {


    @Query(value="SELECT * FROM rating where exemplar_name = ?1", nativeQuery = true)
    List<Rating> findRatingsForExemplar(String exemplar_name);

   @Query(value="Select AVG(rating) from rating where exemplar_name = ?1",
           nativeQuery = true)
   Optional<Double> getAvgRatingForExemlar(String exemplar_name);

   @Query(value="Select  COUNT(*), r.exemplar_name from  rating r where r.sql_date > ?1 group by r.exemplar_name",
           nativeQuery = true)
   Map<String, Integer> getNumberOfRatingsLastWeekByExemplar(String date);
}

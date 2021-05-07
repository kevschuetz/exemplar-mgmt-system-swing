package resource;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExemplarRepository extends JpaRepository<Exemplar, String> {



    @Query(value = "Select * from exemplar e where e.creator_username = ?1",
        nativeQuery = true)
    List<Exemplar> findExemplarsForCreator(String creator);

    @Query(value = "select e.name, e.problem, e.solution, e.creator_username from exemplar e join exemplar_contributors c on e.name = c.exemplar_name\n" +
            "where c.contributors_username =?1", nativeQuery = true)
    List<Exemplar> findExemplarsForContributor(String contributor);


    //UNION Methode, die alle Exemplars zurückgibt, in denen jemand creator oder contributor ist (ober zwei verbinden)

    //Custom funktion ... where name like '%?!%'
}

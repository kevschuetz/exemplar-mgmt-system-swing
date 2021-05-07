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


    @Query(value="(Select * from exemplar e where e.creator_username = ?1) \n" +
            "UNION\n" +
            "(\n" +
            "select e.name, e.problem, e.solution, e.creator_username \n" +
            "from exemplar e join exemplar_contributors c on e.name = c.exemplar_name\n" +
            "where c.contributors_username =?1)"
                            ,nativeQuery = true)
    List<Exemplar> findExemplarsForUser(String user);

    @Query(value="select * from exemplar where name like ?1", nativeQuery = true)
    List<Exemplar> findExemplarsNameLikeXY(String search);


    @Query(value="select * from exemplar e join exemplar_labels l on l.exemplar_name = e.name where labels_value = ?1", nativeQuery = true)
    List<Exemplar> findExemplarsByLabels(String label);
}

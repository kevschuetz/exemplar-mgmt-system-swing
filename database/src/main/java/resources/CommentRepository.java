package resources;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, String> {

    @Query(value = "Select * from comment c where c.exemplar_name = ?1",
            nativeQuery = true)
    List<Comment> findCommentsForExemplar(String exemplar);
}

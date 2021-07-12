package resource;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "Select * from comment c where c.exemplar_name = ?1 AND c.id NOT IN (SELECT answers_id FROM comment_answers)",
            nativeQuery = true)
    List<Comment> findCommentsForExemplar(String exemplar);
}

package resources;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface UserRepository extends JpaRepository<User, String> {

    @Query(value="select * from users where username like ?1", nativeQuery = true)
    List<User> searchUsers(String value);
}

package resources;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommunityRepository extends JpaRepository<Community, String> {

    @Query(value = "Select c.name, c.creator_username from community c join community_members m" +
            "                               on c.name = m.community_name" +
            "                   where m.members_username = ?1" +
            "                   group by c.name, c.creator_username, m.members_username",
    nativeQuery = true)
    List<Community> getCommunitiesForUser(String member);

}

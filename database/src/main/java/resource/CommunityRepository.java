package resource;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommunityRepository extends JpaRepository<Community, String> {

    @Query(value = "Select c.name, c.creator_username from community c join community_members m" +
            "                               on c.name = m.community_name" +
            "                   where m.members_username = ?1" +
            "                   group by c.name, c.creator_username, m.members_username" +
            "                   UNION" +
            "                   Select c1.name, c1.creator_username from community c1 where c1.creator_username = ?1",
    nativeQuery = true)
    List<Community> getCommunitiesForUser(String member);


    @Query(value = "Select * from community where name like ?1", nativeQuery = true)
    List<Community> searchCommunity_NameLikeXY(String search);

}

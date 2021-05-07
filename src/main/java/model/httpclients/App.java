package model.httpclients;

import model.entities.Community;
import model.entities.Exemplar;
import model.entities.Rating;
import model.entities.User;

import java.util.List;

public class App {
    public static void main(String[] args) {
        ExemplarClient exemplarClient = new ExemplarClient();
        List<Exemplar> exemplars = exemplarClient.getExemplarsForUser("admin");
        System.out.println(exemplars);
        exemplars= exemplarClient.searchExemplars("e");
        System.out.println(exemplars);
        exemplars= exemplarClient.searchExemplarsByLabel("java");
        System.out.println(exemplars);

        CommunityClient communityClient = new CommunityClient();
        List<Community> communities = communityClient.getCommunitiesForUser("admin");
        System.out.println(communities);
        communities= communityClient.searchCommunities("1");
        System.out.println(communities);

        UserClient userClient = new UserClient();
        List<User> users = userClient.searchUsers("ad");

        RatingClient ratingClient = new RatingClient();
        double avg = ratingClient.getAvgRatingForExemplar("exemplar1");
        System.out.println(avg);

        List<Rating> ratingsFor = ratingClient.getRatingsForExemplar("exemplar1");
        for(Rating r : ratingsFor){
            System.out.println(r.getKey().getExemplar().getName());
        }
    }


}

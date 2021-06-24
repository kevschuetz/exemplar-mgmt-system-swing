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

        exemplars= exemplarClient.searchExemplars("e");

        exemplars= exemplarClient.searchExemplarsByLabel("java");


        CommunityClient communityClient = new CommunityClient();
        List<Community> communities = communityClient.getCommunitiesForUser("admin");

        communities= communityClient.searchCommunities("1");


        UserClient userClient = new UserClient();


        RatingClient ratingClient = new RatingClient();
        double avg = ratingClient.getAvgRatingForExemplar("exemplar1");


        List<Rating> ratingsFor = ratingClient.getRatingsForExemplar("exemplar1");
        for(Rating r : ratingsFor){

        }
    }


}

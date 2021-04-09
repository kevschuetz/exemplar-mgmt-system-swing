package resources;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rating")
public class RatingController {
    private RatingRepository repository;
    public RatingController(RatingRepository repository){
        this.repository=repository;
    }

    @GetMapping("")
    public List<Rating> getRatings(){
        return repository.findAll();
    }

    @PutMapping(value="", consumes = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public Rating addRating(@RequestBody Rating r) {
        System.out.println(r.getKey().getExemplar().getName());
        System.out.println(r.getRating());
        System.out.println(r.getKey().getUser().getUsername());

        return repository.save(r);
    }


    @GetMapping("/{name}")
    public Rating getRating(@RequestParam String username, @RequestParam String exemplarname){
        Exemplar e = ExemplarController.getRepository().findById(exemplarname).get();
        User u = UserController.getUserRepository().findById(username).get();
        RatingPK key = new RatingPK();
        key.setExemplar(e);
        key.setUser(u);
        Optional<Rating> result = repository.findById(key);
        return result.orElse(null);
    }

    @DeleteMapping("/{value}")
    public void deleteRating(@RequestParam String username, @RequestParam String exemplarname){
        Exemplar e = ExemplarController.getRepository().findById(exemplarname).get();
        User u = UserController.getUserRepository().findById(username).get();
        RatingPK key = new RatingPK();
        key.setExemplar(e);
        key.setUser(u);
        Rating k = repository.findById(key).orElse(null);
        if (k != null) repository.delete(k);
    }

}

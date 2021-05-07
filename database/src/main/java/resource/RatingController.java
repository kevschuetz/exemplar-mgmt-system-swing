package resource;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ratings")
public class RatingController {
    private RatingRepository repository;
    public RatingController(RatingRepository repository){
        this.repository=repository;
    }

    @GetMapping("")
    public List<Rating> getRatings(){
        return repository.findAll();
    }

    @PostMapping(value="", consumes = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public Rating addRating(@RequestBody Rating r) {
        Optional<Rating> rating = repository.findById(r.getKey());
        if(rating.isPresent()){
            return repository.save(r);
        } else return repository.save(r);
    }

    @PutMapping(value="/{exemplarName}", consumes = {"application/json"})
    public Rating updateRating(@RequestBody Rating r, @PathVariable String exemplarName, @RequestParam String userName){
        Optional<Exemplar> exemplar = ExemplarController.getRepository().findById(exemplarName);
        if(exemplar.isPresent()){
            Optional<User> user = UserController.getUserRepository().findById(userName);
            if(user.isPresent()){
               RatingPK key = new RatingPK();
               key.setExemplar(exemplar.get());
               key.setUser(user.get());
               Optional<Rating> rating = repository.findById(key);
               if(rating.isPresent()){
                   repository.delete(rating.get());
                   return repository.save(r);
               }else return null;
            }else return null;
        } else return null;
    }


    @GetMapping("/{exemplarname}")
    public Rating getRating(@RequestParam String username, @PathVariable String exemplarname){
        Exemplar e = ExemplarController.getRepository().findById(exemplarname).get();
        User u = UserController.getUserRepository().findById(username).get();
        RatingPK key = new RatingPK();
        key.setExemplar(e);
        key.setUser(u);
        Optional<Rating> result = repository.findById(key);
        return result.orElse(null);
    }

    @DeleteMapping(value = "/{exemplarname}")
    public void deleteRating(@RequestParam String username, @PathVariable String exemplarname){
        Exemplar e = ExemplarController.getRepository().findById(exemplarname).get();
        User u = UserController.getUserRepository().findById(username).get();
        RatingPK key = new RatingPK();
        key.setExemplar(e);
        key.setUser(u);
        Rating k = repository.findById(key).orElse(null);
        if (k != null){
            repository.delete(k);
        }
    }

    @GetMapping(value="/forexemplar")
    public List<Rating> getRatingsForExemplar(@RequestParam String exemplarname){
        return repository.findRatingsForExemplar(exemplarname);
    }

}

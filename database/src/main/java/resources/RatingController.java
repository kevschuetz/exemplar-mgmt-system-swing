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
        return repository.save(r);
    }


    @GetMapping("/{name}")
    public Rating getRating(@PathVariable RatingPK key){
        Optional<Rating> result = repository.findById(key);
        return result.orElse(null);
    }

    @DeleteMapping("/{value}")
    public void deleteRating(@PathVariable RatingPK key){
        Rating k = repository.findById(key).orElse(null);
        if (k != null) repository.delete(k);
    }

}

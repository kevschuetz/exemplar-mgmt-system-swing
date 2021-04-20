package resources;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/community")
public class CommunityController {
    private CommunityRepository repository;

    public CommunityController(CommunityRepository repository){
        this.repository=repository;
    }

    @GetMapping("")
    public List<Community> getCommunities(){
        return repository.findAll();
    }

    @PutMapping(value="", consumes = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public Community addCommunity(@RequestBody Community c) {
        return repository.save(c);
    }


    @GetMapping("/{name}")
    public Community getCommunity(@PathVariable String name){
        Optional<Community> result = repository.findById(name);
        return result.orElse(null);
    }

    @DeleteMapping("/{name}")
    public void deleteCommunity(@PathVariable String name){
        Community e = repository.findById(name).orElse(null);
        if (e != null) repository.delete(e);
    }


}

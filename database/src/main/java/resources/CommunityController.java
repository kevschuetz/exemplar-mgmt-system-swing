package resources;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/communities")
public class CommunityController {
    private CommunityRepository repository;

    public CommunityController(CommunityRepository repository){
        this.repository=repository;
    }

    @GetMapping("")
    public List<Community> getCommunities(){
        return repository.findAll();
    }

    @PostMapping(value="", consumes = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public Community addCommunity(@RequestBody Community c) {
        Optional<Community> community = repository.findById(c.getName());
        if(community.isPresent()){
            return null;
        }
        else return repository.save(c);
    }

    @PutMapping(value="/{name}", consumes = {"application/json"})
    public Community updateCommunity(@RequestBody Community c, @PathVariable String name){
        Optional<Community> community = repository.findById(name);
        if (community.isPresent()){
            repository.delete(community.get());
            return repository.save(c);
        }
        else return null;
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

package resource;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/exemplars")
public class ExemplarController {
    public static ExemplarRepository repository;

    public ExemplarController(ExemplarRepository exemplarRepository){
        this.repository=exemplarRepository;
    }

    public static ExemplarRepository getRepository() {
        return repository;
    }

    public void setRepository(ExemplarRepository repository) {
        this.repository = repository;
    }

    @GetMapping("")
    public List<Exemplar> getExemplars(){
         return repository.findAll();
     }

    @PostMapping(value="", consumes = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public Exemplar addExemplar(@RequestBody Exemplar e) {
        Optional<Exemplar> exemplar = repository.findById(e.getName());
        if(exemplar.isPresent()){
            return null;
        } else return repository.save(e);
    }


    @PutMapping(value="/{name}", consumes = {"application/json"})
    public Exemplar updateExemplar(@RequestBody Exemplar e, @PathVariable String name){
        Optional<Exemplar> exemplar = repository.findById(name);
        if(exemplar.isPresent()){
            return repository.save(e);
        } else return null;
    }

    @GetMapping("/{name}")
    public Exemplar getExemplar(@PathVariable String name){
        Optional<Exemplar> result = repository.findById(name);
        return result.orElse(null);
    }

    @DeleteMapping("/{name}")
    public void deleteExemplar(@PathVariable String name){
        Exemplar e = repository.findById(name).orElse(null);
        if (e != null) repository.delete(e);
    }

    @GetMapping("/creator")
    public List<Exemplar> findExemplarsForCreator(@RequestParam String creator){
        System.out.println("arrived");
        return repository.findExemplarsForCreator(creator);
    }

    @GetMapping("/contributor")
    public List<Exemplar> findExemplarsForContributor(@RequestParam String contributor){
        return repository.findExemplarsForContributor(contributor);
    }

    @GetMapping("/user")
    public List<Exemplar> findExemplarsForUser(@RequestParam String user){
        return repository.findExemplarsForUser(user);
    }

    @GetMapping("/search")
    public List<Exemplar> findExemplarsNameLikeXY(@RequestParam String value){
        String search = "%";
        search = search + value;
        search = search + "%";
        System.out.println(search);
        return repository.findExemplarsNameLikeXY(search);
    }
}

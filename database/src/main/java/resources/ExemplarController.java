package resources;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/exemplar")
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

    @PutMapping(value="", consumes = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public Exemplar addExemplar(@RequestBody Exemplar e) {
        return repository.save(e);
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
}

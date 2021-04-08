package main;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exemplar")
public class ExemplarController {
    public ExemplarRepository exemplarRepository;

    public ExemplarController(ExemplarRepository exemplarRepository){
        this.exemplarRepository=exemplarRepository;
    }

     @GetMapping("")
    public List<Exemplar> getExemplars(){
         return exemplarRepository.findAll();
     }

    @PutMapping(value="", consumes = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public Exemplar addExemplar(@RequestBody Exemplar e) {
        return exemplarRepository.save(e);
    }
}

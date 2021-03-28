package application;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exemplar")
public class ExemplarController {
    private ExemplarRepository exemplarRepository;

    public ExemplarController(ExemplarRepository exemplarRepository){
        this.exemplarRepository=exemplarRepository;
    }

     @GetMapping("")
    public Iterable<Exemplar> getExemplar(){
         return exemplarRepository.findAll();
     }



}

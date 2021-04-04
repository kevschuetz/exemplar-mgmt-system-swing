package rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/exemplar")
public class ExemplarController {
    private ExemplarRepository exemplarRepository;

    public ExemplarController(ExemplarRepository exemplarRepository){
        this.exemplarRepository=exemplarRepository;
    }

     @GetMapping("")
    public List<Exemplar> getExemplar(){
         return exemplarRepository.findAll();
     }



}

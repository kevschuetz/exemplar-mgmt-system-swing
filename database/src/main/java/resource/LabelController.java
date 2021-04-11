package resource;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/label")
public class LabelController {
    private LabelRepository repository;

    public LabelController(LabelRepository repository){
        this.repository=repository;
    }

    @GetMapping("")
    public List<Label> getLabels(){
        return repository.findAll();
    }

    @PutMapping(value="", consumes = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public Label addLabel(@RequestBody Label l) {
        return repository.save(l);
    }


    @GetMapping("/{name}")
    public Label getLabel(@PathVariable String value){
        Optional<Label> result = repository.findById(value);
        return result.orElse(null);
    }

    @DeleteMapping("/{value}")
    public void deleteLabel(@PathVariable String value){
        Label l = repository.findById(value).orElse(null);
        if (l != null) repository.delete(l);
    }



}

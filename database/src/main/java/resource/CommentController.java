package resource;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/comments")
public class CommentController {

    public static CommentRepository repository;

    public CommentController(CommentRepository commentRepository){
        this.repository=commentRepository;
    }

    public static CommentRepository getRepository() {
        return repository;
    }

    public void setRepository(CommentRepository repository) {
        this.repository = repository;
    }

    @GetMapping("")
    public List<Comment> getComments(){
        return repository.findAll();
    }

    @PostMapping(value="", consumes = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public Comment addComment(@RequestBody Comment c) {
       /* Optional<Comment> comment = repository.findById(c.getId());
        if(comment.isPresent()){
            return null;
        } else */ return repository.save(c);
    }


    @PutMapping(value="/{id}", consumes = {"application/json"})
    public Comment updateComment(@RequestBody Comment c, @PathVariable int id){
        Optional<Comment> comment = repository.findById(c.getId());
        if(comment.isPresent()){
            return repository.save(c);
        } else return null;
    }

    @GetMapping("/{id}")
    public Comment getComment(@PathVariable long id){
        Optional<Comment> result = repository.findById(id);
        return result.orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable long id){
        Comment c = repository.findById(id).orElse(null);
        if (c != null) repository.delete(c);
    }

    @GetMapping("/forexemplar")
    public List<Comment> findCommentsForExemplar(@RequestParam String exemplarname){
        List<Comment> result = repository.findCommentsForExemplar(exemplarname);
        return result;
    }
}

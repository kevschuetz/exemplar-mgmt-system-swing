package resources;

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
        Optional<Comment> comment = repository.findById(Integer.toString(c.getId()));
        if(comment.isPresent()){
            return null;
        } else return repository.save(c);
    }


    @PutMapping(value="/{id}", consumes = {"application/json"})
    public Comment updateComment(@RequestBody Comment c, @PathVariable int id){
        Optional<Comment> comment = repository.findById(Integer.toString(c.getId()));
        if(comment.isPresent()){
            return repository.save(c);
        } else return null;
    }

    @GetMapping("/{id}")
    public Comment getComment(@PathVariable int id){
        Optional<Comment> result = repository.findById(Integer.toString(id));
        return result.orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable int id){
        Comment c = repository.findById(Integer.toString(id)).orElse(null);
        if (c != null) repository.delete(c);
    }

    @GetMapping("/exemplar")
    public List<Comment> findCommentsForExemplar(@RequestParam String exemplar){
        System.out.println("arrived");
        return repository.findCommentsForExemplar(exemplar);
    }
}

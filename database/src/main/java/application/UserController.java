package application;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/user")
public class UserController {
    private UserRepository userRepository;

    public UserController(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    @PutMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Users createUser(@RequestBody Users u) {
        return userRepository.save(u);
    }

    @GetMapping("")
    public List<Users> getUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{username}")
    public Users getUser(@PathVariable String username){
        Optional<Users> result = userRepository.findById(username);
        return result.orElse(null);
    }

    @DeleteMapping("/{username}")
    public void deleteUser(@PathVariable String username){
        Users u = userRepository.findById(username).orElse(null);
        if (u != null) userRepository.delete(u);
    }
}

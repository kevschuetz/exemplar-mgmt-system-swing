package resources;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/users")
public class UserController {
    private static UserRepository userRepository;

    public UserController(UserRepository userRepository){
        UserController.userRepository = userRepository;
    }

    public static UserRepository getUserRepository() {
        return userRepository;
    }

    public static void setUserRepository(UserRepository userRepository) {
        UserController.userRepository = userRepository;
    }

    @PostMapping(value="", consumes = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public User addUser(@RequestBody User u) {
        return userRepository.save(u);
    }

    @GetMapping("")
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{username}")
    public User getUser(@PathVariable String username){
        Optional<User> result = userRepository.findById(username);
        return result.orElse(null);
    }

    @DeleteMapping("/{username}")
    public void deleteUser(@PathVariable String username){
        User u = userRepository.findById(username).orElse(null);
        if (u != null) userRepository.delete(u);
    }
}

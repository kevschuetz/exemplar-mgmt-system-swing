package appliation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private  UserRepository userRepository;

    public UserController(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Users createUser(@RequestBody Users u) {
        System.out.println("Username: " +  u.getUsername());
        System.out.println("createUser");
        return userRepository.save(u);
    }

    @GetMapping("")
    public Iterable<Users> getUsers() {
        System.out.println("getUsers");
        return userRepository.findAll();
    }
}

package cloud.qasino.card.controller;

import cloud.qasino.card.entity.User;
import cloud.qasino.card.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@Slf4j
@RestController
public class UserController {

    private UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // normal CRUD

    @PostMapping(value = "/users/{alias}", params={"email"})
    public ResponseEntity<User> addUser(
            @PathVariable("alias") String alias,
            @RequestParam("email") String email
            //,BindingResult result
            //,Model model
            ) {

        // if (result.hasErrors()) {return "users"; }

        User createdUser = userRepository.save(new User(alias, email));
        if (createdUser == null) {
            return ResponseEntity.notFound().build();
        } else {
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(createdUser.getUserId())
                    .toUri();

            return ResponseEntity.created(uri).body(createdUser);

            // model.addAttribute("users", userRepository.findAll());
            // return "index";
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Optional<User>> getUser(
            @PathVariable("id") int id
            //, Model model
            ) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        } else {
            Optional<User> foundUser = userRepository.findById(id);
            return ResponseEntity.ok(foundUser);
        }

        // model.addAttribute("user", user);
        // return "users";
    }

    @PostMapping(value = "/users/{id}", params={"alias", "email"})
    public ResponseEntity<User> updateUser(
            @PathVariable("id") int id,
            @RequestParam("alias") String alias,
            @RequestParam("email") String email
            //,BindingResult result
            //,Model model
            ) {
        // if (result.hasErrors()) {return "users"; }

        Optional<User> foundUser = userRepository.findById(id);

        if (foundUser == null) {
            return ResponseEntity.notFound().build();
        } else {
            User updateUser = foundUser.get();
            updateUser.setAlias(alias);
            updateUser.setEmail(email);
            userRepository.save(updateUser);

            return ResponseEntity.ok(updateUser);
        }

        // model.addAttribute("users", userRepository.findAll());
        // return "index";
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<User> deleteUser(
            @PathVariable("id") int id
            // ,Model model
            ) {
        userRepository.deleteById(id);

        return ResponseEntity.noContent().build();
        // model.addAttribute("users", userRepository.findAll());
        //return "index";
    }
}

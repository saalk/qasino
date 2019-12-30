package cloud.qasino.card.controller;

import cloud.qasino.card.entity.User;
import cloud.qasino.card.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
    public String addUser(
            @PathVariable("alias") String alias,
            @RequestParam("email") String email,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "users";
        }

        User user = new User(alias, email);
        userRepository.save(user);

        model.addAttribute("users", userRepository.findAll());
        return "index";
    }

    @GetMapping("/users/{id}")
    public String showUpdateForm(@PathVariable("id") int id, Model model) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));

        model.addAttribute("user", user);
        return "users";
    }

    @PostMapping(value = "/users/{id}", params={"alias", "email"})
    public String updateUser(
            @PathVariable("id") int id,
            @RequestParam("alias") String alias,
            @RequestParam("email") String email,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "users";
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));

        user.setAlias(alias);
        user.setEmail(email);

        userRepository.save(user);
        model.addAttribute("users", userRepository.findAll());
        return "index";
    }

    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable("id") int id, Model model) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        userRepository.delete(user);
        model.addAttribute("users", userRepository.findAll());
        return "index";
    }
}

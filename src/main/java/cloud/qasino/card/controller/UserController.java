package cloud.qasino.card.controller;

import cloud.qasino.card.entity.User;
import cloud.qasino.card.repository.UserRepository;
import org.antlr.v4.runtime.misc.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class UserController {

    private UserRepository userRepository;

    @GetMapping("/signup")
    public String showSignUpForm(User user) {
        return "add-user";
    }

    @PostMapping("/adduser")
    public String addUser(@Valid User signup, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "adduser";
        }
        User user = new User(signup.getAlias());
        user.setEmail(signup.getEmail());

        userRepository.save(user);
        model.addAttribute("users", userRepository.findAll());
        return "play-game";
    }

    // additional CRUD methods
}

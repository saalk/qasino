package cloud.qasino.card.controller;

import cloud.qasino.card.entity.Game;
import cloud.qasino.card.entity.Player;
import cloud.qasino.card.entity.User;
import cloud.qasino.card.entity.enums.AiLevel;
import cloud.qasino.card.repositories.GameRepository;
import cloud.qasino.card.repositories.PlayerRepository;
import cloud.qasino.card.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class PlayerController {

    private PlayerRepository playerRepository;
    private GameRepository gameRepository;
    private UserRepository userRepository;

    @Autowired
    public PlayerController(
            PlayerRepository playerRepository,
            GameRepository gameRepository,
            UserRepository userRepository) {

        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    // normal CRUD

    @PostMapping(value = "/players/{human}", params={"userId","gameId"})
    public String addPlayer(
            @PathVariable("human") Boolean human,
            @RequestParam("userId") int userId,
            @RequestParam("gameId") int gameId,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "players";
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + userId));

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid game Id:" + gameId));

        Player player = new Player(user, game, human);
        playerRepository.save(player);

        model.addAttribute("players", playerRepository.findAll());
        return "index";
    }

    @GetMapping("/players/{id}")
    public String showUpdateForm(@PathVariable("id") int id, Model model) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid player Id:" + id));

        model.addAttribute("player", player);
        return "players";
    }

    @PostMapping(value = "/players/{id}", params={"avatar", "aiLevel"})
    public String updatePlayer(
            @PathVariable("id") int id,
            @RequestParam("avatar") String avatar,
            @RequestParam("aiLevel") String aiLevel,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "players";
        }

        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid player Id:" + id));

        player.setAvatar(avatar);
        player.setAiLevel(AiLevel.fromLabel(aiLevel));

        playerRepository.save(player);
        model.addAttribute("players", playerRepository.findAll());
        return "index";
    }

    @DeleteMapping("/players/{id}")
    public String deletePlayer(@PathVariable("id") int id, Model model) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid player Id:" + id));
        playerRepository.delete(player);
        model.addAttribute("players", playerRepository.findAll());
        return "index";
    }
}

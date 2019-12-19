package nl.knikit.card.controller;

import nl.knikit.card.entity.example.Parent;
import nl.knikit.card.event.springevent.CustomSpringEvent;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
/**
 * @RestController is a convenience annotation for creating Restful controllers.
 * It is a specialization of @Component and is autodetected through classpath scanning.
 * It adds the @Controller and @ResponseBody annotations. It converts the response to JSON or XML.
 */
public class GamesController {

/*    @Autowired
    @Lazy
    StateMachine<States, Events> stateMachine;*/

    private List<Parent> parentList = new ArrayList<>();
    private Parent parent = new Parent();

//        @Autowired
//        private GamesRepository gr;

    @GetMapping(value = "/games/all", params="type")
    public String findAll(@RequestParam("type") String type) {
        return "Get the list for type=" + type + "list= "+ parentList;
    }

    @GetMapping("/games/{id}")
    public String find(@PathVariable("id") String id) throws Exception {

        String message = "Get a specific Parent with id=" + id;
        CustomSpringEvent event = new CustomSpringEvent(message, message);

/*
        stateMachine.start();
        stateMachine.sendEvent(COIN);
        stateMachine.stop();
*/

        return message;
    }

    @PostMapping(value = "/game")
    public Parent addGame(Parent parent) {
        parentList.add(parent);
        return parent;
    }

    @RequestMapping(value = "*", method = { RequestMethod.GET, RequestMethod.POST})
    public String getFallback() {
        return "Fallback for GET Requests";
    }

//        @RequestMapping("/entity/findby/{id}")
//        public Parent findById(@PathVariable Long id) {
//            for (Parent parent : userList) {
//                if (parent.getId().equals(id)) {
//                    return Optional.of(parent).get();
//                }
//            }
//            return Optional.<Parent>empty().get();
//        }
}


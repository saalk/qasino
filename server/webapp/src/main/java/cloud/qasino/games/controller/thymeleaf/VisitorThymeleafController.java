package cloud.qasino.games.controller.thymeleaf;

import cloud.qasino.games.database.entity.Visitor;
import cloud.qasino.games.database.repository.VisitorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Optional;

// basic path /qasino
// basic header @RequestHeader(value "visitor", required = true) int visitorId" // else 400
//
// 200 - ok
// 201 - created
// 400 - bad request - error/reason "url ... not available"
// 404 - not found - error/message "invalid value x for y" + reason [missing]
// 412 - precondition failed = error/message - "violation of rule z"
// 500 - internal server error

@RestController
//@Api(tags = {WebConfiguration.QASINO_TAG})
@Slf4j
public class VisitorThymeleafController {

    private final VisitorRepository visitorRepository;

    public VisitorThymeleafController(
            VisitorRepository visitorRepository) {
        this.visitorRepository = visitorRepository;

    }


    @GetMapping("visitor/current")
    @ResponseStatus(value = HttpStatus.OK)
//    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public Visitor currentVisitor(Principal principal) {
        Assert.notNull(principal);
        return visitorRepository.findOneByEmail(principal.getName());
    }

    @GetMapping("visitor/{id}")
    @ResponseStatus(value = HttpStatus.OK)
//    @Secured("ROLE_ADMIN")
    public Optional<Visitor> visitor(@PathVariable("id") Long id) {
        return visitorRepository.findVisitorByVisitorId(id);
    }


}
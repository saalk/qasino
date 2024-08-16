package cloud.qasino.games.database.service;

import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.repository.LeagueRepository;
import cloud.qasino.games.database.security.Privilege;
import cloud.qasino.games.database.security.PrivilegeRepository;
import cloud.qasino.games.database.security.Role;
import cloud.qasino.games.database.security.RoleRepository;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.database.security.VisitorRepository;
import cloud.qasino.games.dto.model.LeagueDto;
import cloud.qasino.games.dto.model.VisitorDto;
import cloud.qasino.games.dto.mapper.LeagueMapper;
import cloud.qasino.games.dto.mapper.VisitorMapper;
import cloud.qasino.games.dto.request.CreationDto;
import cloud.qasino.games.dto.request.ParamsDto;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Lazy
@Slf4j
public class VisitorAndLeaguesService {

    // @formatter:off
    @Autowired private VisitorRepository visitorRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PrivilegeRepository privilegeRepository;
    @Autowired private LeagueRepository leagueRepository;
//    LeagueMapper leagueMapper;

    private PasswordEncoder encoder;

    public VisitorAndLeaguesService(PasswordEncoder passwordEncoder) {
        this.encoder = passwordEncoder;
    }

    // counts
    Long countByAlias(VisitorDto visitorDto) {
        return visitorRepository.countByAlias(visitorDto.getAlias());
    };
    // find one
    public VisitorDto findByUsername(ParamsDto paramsDto){
        Visitor retrievedVisitor = visitorRepository.findByUsername(paramsDto.getSuppliedVisitorUsername());
        return VisitorMapper.INSTANCE.toDto(retrievedVisitor);
    };
    public VisitorDto findOneByVisitorId(ParamsDto paramsDto) {
        Visitor retrievedVisitor = visitorRepository.getReferenceById(paramsDto.getSuppliedVisitorId());
        return VisitorMapper.INSTANCE.toDto(retrievedVisitor);
    };
    Optional<VisitorDto> findVisitorByAliasAndAliasSequence(VisitorDto visitorDto){
        Optional<Visitor> retrievedVisitor = visitorRepository.findVisitorByAliasAndAliasSequence(visitorDto.getAlias(),visitorDto.getAliasSequence());
        return Optional.ofNullable(retrievedVisitor)
                .filter(Optional::isPresent) // lambda is => visitor -> visitor.isPresent()
                .map(visitor -> VisitorMapper.INSTANCE.toDto(visitor.get()));
    };
    public LeagueDto findOneByLeagueId(ParamsDto paramsDto) {
        League retrievedLeague = leagueRepository.getReferenceById(paramsDto.getSuppliedLeagueId());
        return LeagueMapper.INSTANCE.toDto(retrievedLeague);
    };
    // find many
    Page<VisitorDto> findAllVisitorsWithPage(Pageable pageable){
        Page<Visitor> visitorPage = visitorRepository.findAllVisitorsWithPage(pageable);
        return visitorPage.map(visitor -> VisitorMapper.INSTANCE.toDto(visitor));
    };
    // delete
    void removeUserByUsername(VisitorDto visitorDto) {
        visitorRepository.removeUserByUsername(visitorDto.getUsername());
    };

    // special
    public Optional<String> substringUserFromEmail(final String emailId) {
        return Optional.ofNullable(emailId)
                .filter(email -> email.contains("@"))
                .map(email -> email.replaceAll("(.*?)@.*", "$1"));
    }
    public VisitorDto repayOrPawn(Long visitorId, int balance, int securedLoan) {
        Visitor found = visitorRepository.getReferenceById(visitorId);
        found.setBalance(balance);
        found.setSecuredLoan(securedLoan);
        Visitor saved = visitorRepository.save(found);

        return VisitorMapper.INSTANCE.toDto(saved);
    }
    public VisitorDto updateUser(Long visitorId, CreationDto creation) {
        Visitor found = visitorRepository.getReferenceById(visitorId);
        found.setAlias(creation.getSuppliedAlias());
        found.setEmail(creation.getSuppliedEmail());
//        found.setPassword(encoder.encode(creation.getSuppliedPassword()));
        found.setUsername(creation.getSuppliedUsername());
        Visitor saved = visitorRepository.save(found);
        return VisitorMapper.INSTANCE.toDto(saved);
    }
    public VisitorDto saveNewUser(Visitor user) {
        final Role basicRole = roleRepository.findByName("ROLE_USER");
        user.setRoles(Collections.singleton(basicRole));
        user.setPassword(encoder.encode(user.getPassword()));
        Visitor savedVisitor = visitorRepository.save(user);
        Visitor retrievedVisitor = visitorRepository.getReferenceById(savedVisitor.getVisitorId());
        return VisitorMapper.INSTANCE.toDto(retrievedVisitor);
    }
    public VisitorDto saveNewAdmin(Visitor admin) {
        List<Role> roles = new ArrayList<>();
        roles.add(roleRepository.findByName("ROLE_USER"));
        roles.add(roleRepository.findByName("ROLE_ADMIN"));
        admin.setRoles(roles);
        admin.setPassword(encoder.encode(admin.getPassword()));
        Visitor savedVisitor = visitorRepository.save(admin);
        Visitor retrievedVisitor = visitorRepository.getReferenceById(savedVisitor.getVisitorId());
        return VisitorMapper.INSTANCE.toDto(retrievedVisitor);
    }

    @PostConstruct
    public void initialize() {

        // create initial privileges
        Privilege readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
        Privilege writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");
        // create initial roles
        createRoleIfNotFound("ROLE_ADMIN", Arrays.asList(readPrivilege, writePrivilege));
        createRoleIfNotFound("ROLE_USER", new ArrayList<>());
        // create initial users and admins
        List<Visitor> users = new ArrayList<>();
        users.add(new Visitor.Builder()
                .withUsername("user")
                .withPassword("user")
                .withEmail("user@email.com")
                .withAlias("User")
                .withAliasSequence(1)
                .build());
        List<Visitor> admins = new ArrayList<>();
        admins.add(new Visitor.Builder()
                .withUsername("admin")
                .withPassword("admin")
                .withEmail("admin@email.com")
                .withAlias("Admin")
                .withAliasSequence(1)
                .build());
        for (Visitor visitor : users) {
            createUserIfNotFound(visitor);
        }
        for (Visitor visitor : admins) {
            createAdminIfNotFound(visitor);
        }
    }
    public Privilege createPrivilegeIfNotFound(String name) {
        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilegeRepository.save(privilege);
        }
        return privilege;
    }
    public void createRoleIfNotFound(String name, Collection<Privilege> privileges) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
            role.setPrivileges(privileges);
            roleRepository.save(role);
        }
    }
    public void createUserIfNotFound(Visitor search) {
        Visitor user = visitorRepository.findByUsername(search.getUsername());
        if (user == null) {
            saveNewUser(search);
            log.warn("createUserIfNotFound: {}",user);
        }
    }
    public void createAdminIfNotFound(Visitor search) {
        Visitor admin = visitorRepository.findByUsername(search.getUsername());
        if (admin == null) {
            saveNewAdmin(search);
            log.warn("createAdminIfNotFound: {}",admin);
        }
    }
}

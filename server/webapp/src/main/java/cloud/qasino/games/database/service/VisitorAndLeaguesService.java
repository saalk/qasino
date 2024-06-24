package cloud.qasino.games.database.service;

import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.repository.LeagueRepository;
import cloud.qasino.games.database.security.Privilege;
import cloud.qasino.games.database.security.PrivilegeRepository;
import cloud.qasino.games.database.security.Role;
import cloud.qasino.games.database.security.RoleRepository;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.database.security.VisitorRepository;
import cloud.qasino.games.dto.LeagueDto;
import cloud.qasino.games.dto.VisitorDto;
import cloud.qasino.games.dto.mapper.LeagueMapper;
import cloud.qasino.games.dto.mapper.VisitorMapper;
import cloud.qasino.games.dto.request.IdsDto;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    VisitorMapper visitorMapper;
    LeagueMapper leagueMapper;
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
    public VisitorDto findByUsername(IdsDto idsDto){
        Visitor retrievedVisitor = visitorRepository.findByUsername(idsDto.getSuppliedVisitorUsername());
        return visitorMapper.visitorToVisitorDto(retrievedVisitor);
    };
    public VisitorDto findOneByVisitorId(IdsDto idsDto) {
        Visitor retrievedVisitor = visitorRepository.findOneByVisitorId(idsDto.getSuppliedVisitorId());
        return visitorMapper.visitorToVisitorDto(retrievedVisitor);
    };
    Optional<VisitorDto> findVisitorByAliasAndAliasSequence(VisitorDto visitorDto){
        Optional<Visitor> retrievedVisitor = visitorRepository.findVisitorByAliasAndAliasSequence(visitorDto.getAlias(),visitorDto.getAliasSequence());
        return Optional.ofNullable(retrievedVisitor)
                .filter(Optional::isPresent) // lambda is => visitor -> visitor.isPresent()
                .map(visitor -> visitorMapper.visitorToVisitorDto(visitor.get()));
    };
    public LeagueDto findOneByLeagueId(IdsDto idsDto) {
        League retrievedLeague = leagueRepository.findOneByLeagueId(idsDto.getSuppliedLeagueId());
        return leagueMapper.leagueToLeagueDto(retrievedLeague);
    };

    // find many
    Page<VisitorDto> findAllVisitorsWithPage(Pageable pageable){
        Page<Visitor> visitorPage = visitorRepository.findAllVisitorsWithPage(pageable);
        return visitorPage.map(visitor -> visitorMapper.visitorToVisitorDto(visitor));
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

    @Transactional
    public VisitorDto saveNewUser(VisitorDto userDto) {
        final Role basicRole = roleRepository.findByName("ROLE_USER");
        userDto.setRoles(Collections.singleton(basicRole));
        userDto.setPassword(encoder.encode(userDto.getPassword()));
        Visitor visitor = visitorMapper.visitorDtoToVisitor(userDto);
        Visitor savedVisitor = visitorRepository.save(visitor);
        Visitor retrievedVisitor = visitorRepository.getReferenceById(savedVisitor.getVisitorId());
        return visitorMapper.visitorToVisitorDto(retrievedVisitor);
    }
    @Transactional
    public VisitorDto saveNewAdmin(VisitorDto adminDto) {
        final Role adminRole = roleRepository.findByName("ROLE_ADMIN");
        adminDto.setRoles(Collections.singleton(adminRole));
        adminDto.setPassword(encoder.encode(adminDto.getPassword()));
        Visitor visitor = visitorMapper.visitorDtoToVisitor(adminDto);
        Visitor savedVisitor = visitorRepository.save(visitor);
        Visitor retrievedVisitor = visitorRepository.getReferenceById(savedVisitor.getVisitorId());
        return visitorMapper.visitorToVisitorDto(retrievedVisitor);
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
    @Transactional
    public Privilege createPrivilegeIfNotFound(String name) {
        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilegeRepository.save(privilege);
        }
        return privilege;
    }
    @Transactional
    public void createRoleIfNotFound(String name, Collection<Privilege> privileges) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
            role.setPrivileges(privileges);
            roleRepository.save(role);
        }
    }
    @Transactional
    public void createUserIfNotFound(Visitor search) {
        Visitor user = visitorRepository.findByUsername(search.getUsername());
        if (user == null) {
            VisitorDto visitorDto = visitorMapper.visitorToVisitorDto(user);
            saveNewUser(visitorDto);
        }
        log.warn("createUserIfNotFound: {}",user);
    }
    @Transactional
    public void createAdminIfNotFound(Visitor search) {
        Visitor admin = visitorRepository.findByUsername(search.getUsername());
        if (admin == null) {
            VisitorDto visitorDto = visitorMapper.visitorToVisitorDto(admin);
            saveNewAdmin(visitorDto);
        }
        log.warn("createAdminIfNotFound: {}",admin);
    }
}

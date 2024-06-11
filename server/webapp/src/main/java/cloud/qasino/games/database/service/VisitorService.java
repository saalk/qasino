package cloud.qasino.games.database.service;

import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.security.Privilege;
import cloud.qasino.games.database.security.PrivilegeRepository;
import cloud.qasino.games.database.security.Role;
import cloud.qasino.games.database.security.RoleRepository;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.database.security.VisitorRepository;
import cloud.qasino.games.dto.VisitorDTO;
import cloud.qasino.games.dto.mapper.VisitorMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
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
@Slf4j
public class VisitorService {

    // @formatter:off
    @Autowired private VisitorRepository visitorRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PrivilegeRepository privilegeRepository;
    @Autowired private PlayerRepository playerRepository;
    VisitorMapper visitorMapper;
    private PasswordEncoder encoder;

    public VisitorService(PasswordEncoder passwordEncoder) {
        this.encoder = passwordEncoder;
    }

    // counts
    Long countByAlias(VisitorDTO visitorDTO) {
        return visitorRepository.countByAlias(visitorDTO.getAlias());
    };

    // find one
    VisitorDTO findByUsername(VisitorDTO visitorDTO){
        Visitor retrievedVisitor = visitorRepository.findByUsername(visitorDTO.getUsername());
        return visitorMapper.visitorToVisitorDTO(retrievedVisitor);
    };
    VisitorDTO findOneByVisitorId(VisitorDTO visitorDTO) {
        Visitor retrievedVisitor = visitorRepository.findOneByVisitorId(visitorDTO.getVisitorId());
        return visitorMapper.visitorToVisitorDTO(retrievedVisitor);
    };
    VisitorDTO findOneByEmail(VisitorDTO visitorDTO) {
        Visitor retrievedVisitor = visitorRepository.findOneByEmail(visitorDTO.getEmail());
        return visitorMapper.visitorToVisitorDTO(retrievedVisitor);
    }
    Optional<VisitorDTO> findVisitorByAliasAndAliasSequence(VisitorDTO visitorDTO){
        Optional<Visitor> retrievedVisitor = visitorRepository.findVisitorByAliasAndAliasSequence(visitorDTO.getAlias(),visitorDTO.getAliasSequence());
        return Optional.ofNullable(retrievedVisitor)
                .filter(Optional::isPresent) // lambda is => visitor -> visitor.isPresent()
                .map(visitor -> visitorMapper.visitorToVisitorDTO(visitor.get()));
    };

    // find many
    Page<VisitorDTO> findAllVisitorsWithPage(Pageable pageable){
        Page<Visitor> visitorPage = visitorRepository.findAllVisitorsWithPage(pageable);
        return visitorPage.map(visitor -> visitorMapper.visitorToVisitorDTO(visitor));
    };

    // delete
    void removeUserByUsername(VisitorDTO visitorDTO) {
        visitorRepository.removeUserByUsername(visitorDTO.getUsername());
    };

    // special
    public Optional<String> substringUserFromEmail(final String emailId) {
        return Optional.ofNullable(emailId)
                .filter(email -> email.contains("@"))
                .map(email -> email.replaceAll("(.*?)@.*", "$1"));
    }

    @Transactional
    public VisitorDTO saveNewUser(VisitorDTO userDTO) {
        final Role basicRole = roleRepository.findByName("ROLE_USER");
        userDTO.setRoles(Collections.singleton(basicRole));
        userDTO.setPassword(encoder.encode(userDTO.getPassword()));
        Visitor visitor = visitorMapper.visitorDTOToVisitor(userDTO);
        Visitor savedVisitor = visitorRepository.save(visitor);
        Visitor retrievedVisitor = visitorRepository.getReferenceById(savedVisitor.getVisitorId());
        return visitorMapper.visitorToVisitorDTO(retrievedVisitor);
    }
    @Transactional
    public VisitorDTO saveNewAdmin(VisitorDTO adminDTO) {
        final Role adminRole = roleRepository.findByName("ROLE_ADMIN");
        adminDTO.setRoles(Collections.singleton(adminRole));
        adminDTO.setPassword(encoder.encode(adminDTO.getPassword()));
        Visitor visitor = visitorMapper.visitorDTOToVisitor(adminDTO);
        Visitor savedVisitor = visitorRepository.save(visitor);
        Visitor retrievedVisitor = visitorRepository.getReferenceById(savedVisitor.getVisitorId());
        return visitorMapper.visitorToVisitorDTO(retrievedVisitor);
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
            VisitorDTO visitorDTO = visitorMapper.visitorToVisitorDTO(user);
            saveNewUser(visitorDTO);
        }
        log.warn("createUserIfNotFound: {}",user);
    }
    @Transactional
    public void createAdminIfNotFound(Visitor search) {
        Visitor admin = visitorRepository.findByUsername(search.getUsername());
        if (admin == null) {
            VisitorDTO visitorDTO = visitorMapper.visitorToVisitorDTO(admin);
            saveNewAdmin(visitorDTO);
        }
        log.warn("createAdminIfNotFound: {}",admin);
    }
}

package cloud.qasino.games.database.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@Secured("ROLE_USER")
public class VisitorService {

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private VisitorRepository visitorRepository;

    private PasswordEncoder encoder;

    public VisitorService(PasswordEncoder passwordEncoder) {
        this.encoder = passwordEncoder;
    }

    @Transactional
    public Visitor saveUser(Visitor user) {
        final Role basicRole = roleRepository.findByName("ROLE_USER");
        user.setRoles(Collections.singleton(basicRole));
        user.setPassword(encoder.encode(user.getPassword()));
        visitorRepository.save(user);
        return user;
    }

    @Transactional
    public Visitor saveAdmin(Visitor admin) {
        final Role adminRole = roleRepository.findByName("ROLE_ADMIN");
        admin.setRoles(Collections.singleton(adminRole));
        admin.setPassword(encoder.encode(admin.getPassword()));
        visitorRepository.save(admin);
        return admin;
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
    @javax.transaction.Transactional
    public Visitor createUserIfNotFound(Visitor search) {
        Visitor visitor = visitorRepository.findByUsername(search.getUsername());
        if (visitor == null) {
            return saveUser(search);
        }
        return  search;
    }

    @Transactional
    public Visitor createAdminIfNotFound(Visitor search) {
        Visitor visitor = visitorRepository.findByUsername(search.getUsername());
        if (visitor == null) {
            return saveAdmin(search);
        }
        return search;
    }

}

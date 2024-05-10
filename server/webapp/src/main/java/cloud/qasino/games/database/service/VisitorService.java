package cloud.qasino.games.database.service;

import cloud.qasino.games.database.entity.Visitor;
import cloud.qasino.games.database.repository.VisitorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class VisitorService implements UserDetailsService {

    @Autowired
    private VisitorRepository visitorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    private final Map<String, Visitor> visitorRegistry = new HashMap<>();

    @PostConstruct
    public void init() {
        visitorRegistry.put("user", new Visitor.Builder()
                .withUsername("user")
                .withPassword(passwordEncoder.encode("user"))
                .withAuthorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .withEmail("user@email.com")
                .withAlias("User")
                .withAliasSequence(1)
                .build());
        visitorRegistry.put("admin", new Visitor.Builder()
                .withUsername("admin")
                .withPassword(passwordEncoder.encode("admin"))
                .withAuthorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .withEmail("admin@email.com")
                .withAlias("Admin")
                .withAliasSequence(1)
                .build());
    }

    @Transactional
    public Visitor save(Visitor account) {
//		account.setPassword(passwordEncoder.encode(account.getPassword()));
        visitorRepository.save(account);
        return account;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Visitor userDetails = visitorRegistry.get(username);
        if (userDetails == null) {
            throw new UsernameNotFoundException("User [" + username + "] not found");
        }
        return userDetails;
    }

    public void signin(Visitor visitor) {
        SecurityContextHolder.getContext().setAuthentication(authenticate(visitor));
    }

    private Authentication authenticate(Visitor visitor) {
        return new UsernamePasswordAuthenticationToken(createUser(visitor), null, Collections.singleton(createAuthority(visitor)));
    }

    private User createUser(Visitor visitor) {
        return new User(visitor.getUsername(), visitor.getPassword(), Collections.singleton(createAuthority(visitor)));
    }

    private GrantedAuthority createAuthority(Visitor visitor) {
        return new SimpleGrantedAuthority(visitor.getAuthorities().toString());
    }

}

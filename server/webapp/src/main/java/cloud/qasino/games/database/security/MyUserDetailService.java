package cloud.qasino.games.database.security;

import cloud.qasino.games.dto.VisitorDto;
import cloud.qasino.games.pattern.singleton.OnlineVisitorsPerDay;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
//@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@Slf4j
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private VisitorRepository visitorRepository;

    private final Map<String, Visitor> userMap = new HashMap<>();

    public MyUserDetailService(BCryptPasswordEncoder bCryptPasswordEncoder) {
   }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Visitor visitor = visitorRepository.findByUsername(username);
        if (visitor == null) {
            throw new UsernameNotFoundException("Visitor [" + username + "] not found");
        } else {
            log.warn("visitor found: {}", visitor);
            OnlineVisitorsPerDay.getInstance().newLogon();

        }
        MyUserPrincipal principal = new MyUserPrincipal(visitor);
        log.warn("Principal found: {}", principal.toString());
        return principal;
    }

    public UserDetails loadUserByUsername(VisitorDto visitorDto) throws UsernameNotFoundException {
        final Visitor visitor = visitorRepository.findByUsername(visitorDto.getUsername());
        log.warn("visitor found: {}",visitor.toString());
        if (visitor == null) {
            throw new UsernameNotFoundException("Visitor [" + visitorDto.getUsername() + "] not found");
        }
        MyUserPrincipal principal = new MyUserPrincipal(visitor);
        log.warn("Principal found: {}", principal.toString());
        return principal;
    }

    public void signin(Visitor visitor) {
        SecurityContextHolder.getContext().setAuthentication(authenticate(visitor));
    }

    private Authentication authenticate(Visitor visitor) {

        return new UsernamePasswordAuthenticationToken(visitorRepository.findByUsername(visitor.getUsername()), null, Collections.singleton(createAuthority(visitor)));
    }

    private GrantedAuthority createAuthority(Visitor visitor) {
        return new SimpleGrantedAuthority(visitor.getRoles().toString());
    }
}

package cloud.qasino.games.database.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MyUserPrincipal implements UserDetails {

    private static final long serialVersionUID = 1L;

    private final Visitor visitor;

    public MyUserPrincipal(Visitor visitor) {
        this.visitor = visitor;
    }

    @Override
    public String getUsername() {
        return visitor.getUsername();
    }

    @Override
    public String getPassword() {
        return visitor.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final List<GrantedAuthority> authorities = new ArrayList<>();
        for (final Role roles : visitor.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(roles.getName()));
        }
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Visitor getVisitor() {
        return visitor;
    }
}

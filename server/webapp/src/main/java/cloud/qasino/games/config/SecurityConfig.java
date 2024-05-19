package cloud.qasino.games.config;

import cloud.qasino.games.database.security.MyUserDetailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
class SecurityConfig {

    // https://www.marcobehler.com/guides/spring-security
    // https://github.com/thymeleaf/thymeleafexamples-layouts/blob/master/src/main/java/thymeleafexamples/layouts/config/SecurityConfig.java

    @Value("${spring.security.debug:false}")
    boolean securityDebug;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.debug(securityDebug).ignoring().requestMatchers("/cssandjs/**", "/images/**", "/authenticate/**", "/homeNotSignedIn", "/general", "/h2-console/**");
    }

    //    loginPage() – the custom login page
    //    loginProcessingUrl() – the URL to submit the username and password to
    //    defaultSuccessUrl() – the landing page after a successful login
    //    failureUrl() – the landing page after an unsuccessful login
    //    logoutUrl() – the custom logout
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults())
                .authorizeHttpRequests(
                        auth -> auth.requestMatchers("/", "/cssandjs/**", "/images/**", "/authenticate/**", "/signin", "/signup", "/general", "/homeNotSignedIn", "/h2-console/**")
                                .permitAll()
                                .requestMatchers("/users/**", "/apps/**").hasAuthority("ADMIN")
                                .requestMatchers("/myapps/**").hasAuthority("CLIENT")
                                .anyRequest()
                                .authenticated()
                )
                .formLogin(formLogin -> formLogin
                                .loginPage("/signin")
                                .loginProcessingUrl("/perform_signin")
                                .defaultSuccessUrl("/", true)
                                .failureUrl("/signin?error=true")
//                        .failureHandler(authenticationFailureHandler())

                )
//                .rememberMe(rememberMe -> rememberMe.key("remember-me-key"))
                .logout(logout -> logout.logoutUrl("/perform_logout")
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessUrl("/signin?logout"));
//                .logoutSuccessHandler(logoutSuccessHandler());

        return http.build();
    }

//--

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity, MyUserDetailService userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder) throws Exception {
        AuthenticationManagerBuilder auth = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(bCryptPasswordEncoder);
        return auth.build();
    }

    @Bean
    public UserDetailsService userDetailsService(BCryptPasswordEncoder bCryptPasswordEncoder) {
        return new MyUserDetailService(bCryptPasswordEncoder);
    }


}
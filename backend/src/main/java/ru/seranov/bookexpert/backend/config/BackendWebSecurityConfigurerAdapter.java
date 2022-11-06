package ru.seranov.bookexpert.backend.config;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Default;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.seranov.bookexpert.backend.service.db.UserService;
import ru.seranov.bookexpert.backend.service.security.UserDetailsServiceDb;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties
public class BackendWebSecurityConfigurerAdapter {
    @NonNull
    private final UserService userService;

    @Autowired
    public BackendWebSecurityConfigurerAdapter(final @NonNull UserService userService) {
        this.userService = userService;
    }

    @Bean
    @NonNull
    public SecurityFilterChain filterChain(@NonNull final HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf().disable()
                .authorizeHttpRequests((requests) -> requests
                        .antMatchers("/", "/open/**").permitAll()
                        .antMatchers("/actuator/**").permitAll()
                        .antMatchers("/scripts/**").permitAll()
                        .antMatchers("/api/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                )
                .logout(LogoutConfigurer::permitAll);

        return http.build();
    }

    @Bean
    @Default
    @Qualifier("userDetailsServiceInService")
    public UserDetailsService userDetailsServiceInService() {
        return new UserDetailsServiceDb(userService, passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        final BCryptPasswordEncoder bcryptPasswordEncoder = new BCryptPasswordEncoder();
        final DelegatingPasswordEncoder delPasswordEncoder =
                (DelegatingPasswordEncoder) PasswordEncoderFactories.createDelegatingPasswordEncoder();
        delPasswordEncoder.setDefaultPasswordEncoderForMatches(bcryptPasswordEncoder);
        return delPasswordEncoder;
    }
}

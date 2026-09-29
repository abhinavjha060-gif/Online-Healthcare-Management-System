package com.hms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // No UserDetailsService bean is declared here on purpose.
    // AppUserDetailsService is already annotated @Service, so Spring Security
    // auto-detects it as the single UserDetailsService bean. Declaring a second
    // one here (as an earlier version of this file did) makes Spring Security
    // silently refuse to wire authentication at all - every login then fails
    // with "Invalid email or password" no matter what you type.

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler roleBasedSuccessHandler() {
        return (request, response, authentication) -> {
            String redirectUrl = authentication.getAuthorities().stream()
                    .findFirst()
                    .map(a -> switch (a.getAuthority()) {
                        case "ROLE_ADMIN" -> "/admin/dashboard";
                        case "ROLE_DOCTOR" -> "/doctor/dashboard";
                        case "ROLE_PATIENT" -> "/patient/dashboard";
                        default -> "/";
                    })
                    .orElse("/");
            response.sendRedirect(redirectUrl);
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(new AntPathRequestMatcher("/"),
                                  new AntPathRequestMatcher("/register"),
                                  new AntPathRequestMatcher("/register/**"),
                                  new AntPathRequestMatcher("/login"),
                                  new AntPathRequestMatcher("/css/**"),
                                  new AntPathRequestMatcher("/js/**"),
                                  new AntPathRequestMatcher("/h2-console/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/admin/**")).hasRole("ADMIN")
                .requestMatchers(new AntPathRequestMatcher("/doctor/**")).hasRole("DOCTOR")
                .requestMatchers(new AntPathRequestMatcher("/patient/**")).hasRole("PATIENT")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(roleBasedSuccessHandler())
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            // Needed so the built-in H2 database console (a debugging tool) can render in a frame.
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
            .csrf(csrf -> csrf.ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")));

        return http.build();
    }
}

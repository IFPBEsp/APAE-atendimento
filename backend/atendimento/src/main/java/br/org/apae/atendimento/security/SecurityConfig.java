package br.org.apae.atendimento.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final FirebaseAuthenticationFilter firebaseAuthFilter;
    private final MockAuthenticationFilter mockAuthFilter;

    public SecurityConfig(@Autowired(required = false) FirebaseAuthenticationFilter firebaseAuthFilter, @Autowired(required = false) MockAuthenticationFilter mockAuthFilter) {
        this.firebaseAuthFilter = firebaseAuthFilter;
        this.mockAuthFilter = mockAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> {
                })
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers(
                                        "/auth/send-link",
                                        "/h2/**",
                                        "/error",
                                        "/"
                                ).permitAll()
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .anyRequest().authenticated()
                );

        if (firebaseAuthFilter != null) {
            http.addFilterBefore(firebaseAuthFilter, UsernamePasswordAuthenticationFilter.class);
        } else if (mockAuthFilter != null) {
            http.addFilterBefore(mockAuthFilter, UsernamePasswordAuthenticationFilter.class);
        }

        return http.build();
    }
}
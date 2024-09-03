package book.movieservice.config;

import book.movieservice.security.filter.InitAuthFilter;
import book.movieservice.security.filter.JWTAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;


@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final InitAuthFilter initialAuthenticationFilter;
    private final JWTAuthFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        httpSecurity.addFilterBefore(initialAuthenticationFilter, BasicAuthenticationFilter.class);
        httpSecurity.addFilterAfter(jwtAuthenticationFilter, BasicAuthenticationFilter.class);

        httpSecurity.authorizeHttpRequests(c -> c.anyRequest().authenticated());

        return httpSecurity.build();
    }
}
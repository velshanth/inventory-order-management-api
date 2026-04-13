package com.guvi.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
      http.csrf(csrf -> csrf.disable());
      http.authorizeHttpRequests(auth ->
              auth.requestMatchers("/api/auth/**").permitAll()
                      .requestMatchers("/api/categories/**").hasRole("ADMIN")
                      .requestMatchers(HttpMethod.GET,"/api/products/**").hasAnyRole("USER", "ADMIN")
                      .requestMatchers("/api/products/**").hasRole("ADMIN")
                      .requestMatchers(HttpMethod.GET, "/api/orders/myorders").hasAnyRole("USER", "ADMIN")
                      .requestMatchers(HttpMethod.GET, "/api/orders").hasRole("ADMIN")
                      .requestMatchers("/api/orders/{id}").hasRole("ADMIN")
                      .requestMatchers("/api/orders/user/{userId}").hasRole("ADMIN")
                      .requestMatchers("/api/orders/**").hasAnyRole("USER","ADMIN")
                      .anyRequest().authenticated()
      );

    AuthenticationEntryPoint entryPoint = (request, response, authException) -> {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("""
            {
              "status": 401,
              "message": "Unauthorized - Please provide a valid token"
            }
        """);
    };

    AccessDeniedHandler deniedHandler = (request, response, ex) -> {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write("""
            {
              "status": 403,
              "message": "Forbidden - You do not have permission to access this resource"
            }
        """);
    };

    http.exceptionHandling(ex -> ex
            .authenticationEntryPoint(entryPoint)
            .accessDeniedHandler(deniedHandler)
    );

      http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
      return http.build();
}
}

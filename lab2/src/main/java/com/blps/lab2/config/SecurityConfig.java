package com.blps.lab2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/developer-actions/").hasAnyAuthority("DEV_INFO", "APP_INFO")
                        .requestMatchers(HttpMethod.POST, "/developer-actions/").hasAnyAuthority("APP_SUBMIT", "APP_PUBLISH")
                        .requestMatchers(HttpMethod.PUT, "/developer-actions/").hasAuthority("APP_UPDATE")
                        .requestMatchers(HttpMethod.DELETE, "/developer-actions/").hasAuthority("APP_DELETE")

                        .requestMatchers(HttpMethod.POST, "/admin-actions/").hasAnyAuthority("APP_CREATE", "PAYMENT_CREATE")
                        .requestMatchers(HttpMethod.PUT, "/admin-actions/").hasAuthority("APP_UPDATE")
                        .requestMatchers(HttpMethod.DELETE, "/admin-actions/").hasAuthority("APP_DELETE")
                        .requestMatchers(HttpMethod.POST, "/admin-actions//moderate").hasAuthority("APP_MODERATE")

                        .requestMatchers(HttpMethod.GET, "/user-actions/").hasAnyAuthority("APP_CATALOG")
                        .requestMatchers(HttpMethod.POST, "/user-actions/").hasAnyAuthority("APP_DOWNLOAD", "APP_USE", "APP_PURCHASE")

                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.withUsername("admin")
                .password("admin")
                .authorities(
                        "APP_CREATE", "APP_UPDATE", "APP_DELETE",
                        "APP_DOWNLOAD", "APP_USE", "APP_MODERATE",
                        "ROLE_ADMIN"
                )
                .build();

        UserDetails developer = User.withUsername("developer")
                .password("password")
                .authorities(
                        "APP_SUBMIT", "APP_UPDATE", "APP_DELETE",
                        "APP_PUBLISH", "DEV_INFO", "APP_INFO",
                        "ROLE_DEVELOPER"
                )
                .build();

        UserDetails user = User.withUsername("user")
                .password("user")
                .authorities(
                        "APP_DOWNLOAD", "APP_USE", "APP_PURCHASE", "APP_CATALOG",
                        "ROLE_USER"
                )
                .build();

        return new InMemoryUserDetailsManager(admin, developer, user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
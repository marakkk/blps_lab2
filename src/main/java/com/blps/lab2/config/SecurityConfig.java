package com.blps.lab2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.GET, "/developer-actions/**").hasRole("DEVELOPER")
                        .requestMatchers(HttpMethod.POST, "/developer-actions/**").hasRole("DEVELOPER")
                        .requestMatchers(HttpMethod.PUT, "/developer-actions/**").hasRole("DEVELOPER")


                        .requestMatchers("/developer-actions/**")
                        .hasRole("DEVELOPER")

                        .requestMatchers(HttpMethod.POST,"/developer-actions/**")
                        .hasAuthority("APP_SUBMIT")

                        .requestMatchers(HttpMethod.PUT,"/developer-actions/**")
                        .hasAuthority("APP_UPDATE")

                        .requestMatchers("/developer-actions/**")
                        .hasAuthority("APP_DELETE")

                        .requestMatchers(HttpMethod.POST,"/developer-actions/**")
                        .hasAuthority("APP_PUBLISH")

                        .requestMatchers("/developer-actions/**")
                        .hasAuthority("DEV_INFO")

                        .requestMatchers("/developer-actions/**")
                        .hasAuthority("APP_INFO")



                        .requestMatchers("/admin-actions/**")
                        .hasRole("ADMIN")

                        .requestMatchers("/admin-actions/**")
                        .hasAuthority("APP_CREATE")

                        .requestMatchers("/admin-actions/**")
                        .hasAuthority("APP_UPDATE")

                        .requestMatchers("/admin-actions/**")
                        .hasAuthority("APP_DELETE")

                        .requestMatchers("/admin-actions/**")
                        .hasAuthority("PAYMENT_CREATE")

                        .requestMatchers("/admin-actions/**")
                        .hasAuthority("APP_MODERATE")



                        .requestMatchers("/user-actions/**")
                        .hasRole("USER")

                        .requestMatchers(HttpMethod.POST,"/user-actions/**")
                        .hasAuthority("APP_DOWNLOAD")

                        .requestMatchers(HttpMethod.POST,"/user-actions/**")
                        .hasAuthority("APP_USE")

                        .requestMatchers("/user-actions/**")
                        .hasAuthority("APP_PURCHASE")

                        .requestMatchers("/user-actions/**")
                        .hasAuthority("APP_CATALOG")

                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new XmlUserDetailsService();
    }
}

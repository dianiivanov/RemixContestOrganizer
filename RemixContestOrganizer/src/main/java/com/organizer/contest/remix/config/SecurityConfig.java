package com.organizer.contest.remix.config;

import com.organizer.contest.remix.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

import static org.springframework.http.HttpMethod.GET;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/downloadFile",
                        "/contest-applications/form", "/submissions/form", "/profile",
                        "/profile/*", "/contests/form", "/submissions/announce").authenticated()
                .antMatchers("/contest-applications","/contest-applications/", "/users").hasRole("ADMIN")
                .and()
                .formLogin();
    }

    @Bean
    public UserDetailsService userDetailsService(UserService userService) {
        return  userService::getUserByUsername;
    }
}

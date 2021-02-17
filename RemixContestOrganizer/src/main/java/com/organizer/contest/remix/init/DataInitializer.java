package com.organizer.contest.remix.init;

import com.organizer.contest.remix.enums.Role;
import com.organizer.contest.remix.models.User;
import com.organizer.contest.remix.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    public static final List<User> users = List.of(
            new User("admin", "adminadmin", "Admin", "admin@gmail.com", Role.ADMIN),
            new User("test", "testtest", "Test", "test@gmail.com", Role.USER)
    );

    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        if(userService.getUsersCount() == 0) {
            users.forEach(user ->{
                PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
                user.setPassword(encoder.encode(user.getPassword()));
                userService.upsertUser(user);
            });
            userService.getAllUsers().forEach(System.out::println);
        }
    }
}

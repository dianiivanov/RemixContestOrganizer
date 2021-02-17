package com.organizer.contest.remix.service.impl;

import com.organizer.contest.remix.dto.UserUpsertDTO;
import com.organizer.contest.remix.enums.Role;
import com.organizer.contest.remix.exception.NonExistingEntityException;
import com.organizer.contest.remix.models.User;
import com.organizer.contest.remix.repository.UserRepository;
import com.organizer.contest.remix.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.validation.constraints.Email;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NonExistingEntityException(
                String.format("There is not user with id=%s",id)));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User deleteUserById(Long id) {
        User userToDelete = getUserById(id);
        userRepository.delete(userToDelete);
        return userToDelete;
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() ->
                new NonExistingEntityException(String.format("Could not find user with username = %s", username)));
    }

    @Override
    public User upsertUser(User user) {
        if (StringUtils.isEmpty(user.getImageUrl())) {
            user.setImageUrl("images/defaultAvatar.jpg");
        }
        return userRepository.save(user);
    }

    @Override
    public User getCurrentUser() {
        //return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return username.equals("anonymousUser") ? null : getUserByUsername(username);
    }

    @Override
    public Long getUsersCount() {
        return userRepository.count();
    }


    public User updateUser(User user) {
        User oldUser = getUserById(user.getId());
        if(!oldUser.getPassword().equals(user.getPassword())) {
            PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
            user.setPassword(encoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserOptionalByUsername(String username){
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> getUserOptionalByEmail(@Email String email){
        return userRepository.findByEmail(email);
    }
}

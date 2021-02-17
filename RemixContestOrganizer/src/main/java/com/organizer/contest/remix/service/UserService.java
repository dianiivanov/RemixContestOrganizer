package com.organizer.contest.remix.service;

import com.organizer.contest.remix.dto.UserUpsertDTO;
import com.organizer.contest.remix.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User getUserById(Long id);
    List<User> getAllUsers();
    User deleteUserById(Long id);
    User getUserByUsername(String username);
    User upsertUser(User user);
    User getCurrentUser();;
    Long getUsersCount();
    Optional<User> getUserOptionalByEmail(String email);
    Optional<User> getUserOptionalByUsername(String username);
}

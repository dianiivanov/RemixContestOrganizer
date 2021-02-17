package com.organizer.contest.remix.web.validators;

import com.organizer.contest.remix.models.User;
import com.organizer.contest.remix.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Optional;

@Component
public class UserDTOValidator {
    @Autowired
    UserService userService;

    public boolean isUserUsernameUsed(Long id, @NotBlank @Size(min=3,max=30) String username){
        Optional<User> userOptional;

        userOptional = userService.getUserOptionalByUsername(username);
        if (id != null && isTheSameUser(id, userOptional)) return false;
        return userOptional.isPresent();
    }

    public boolean isUserEmailUsed(Long id, @Email String email){
        Optional<User> userOptional;

        userOptional = userService.getUserOptionalByEmail(email);
        if (id!=null && isTheSameUser(id, userOptional)) return false;
        return userOptional.isPresent();
    }

    private boolean isTheSameUser(Long id, Optional<User> userOptional) {
        if(userOptional.isPresent()){
            User user = userOptional.get();
            if(user.getId() == id){
                return true;
            }
        }
        return false;
    }
}

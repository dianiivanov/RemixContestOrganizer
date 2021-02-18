package com.organizer.contest.remix.web.controller;

import com.organizer.contest.remix.dto.UserUpsertDTO;
import com.organizer.contest.remix.enums.Role;
import com.organizer.contest.remix.models.User;
import com.organizer.contest.remix.service.UserService;
import com.organizer.contest.remix.web.util.MultipartFileHandleUtil;
import com.organizer.contest.remix.web.validators.UserDTOValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@Controller
public class RegisterController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserDTOValidator userDTOValidator;

    @Autowired
    private MultipartFileHandleUtil multipartFileHandleUtil;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping(path = "/register")
    String getRegistrationPage(@ModelAttribute("userDTO") UserUpsertDTO userDTO,
                               @RequestParam(value = "mode", required = false) String mode,
                               @RequestParam(value = "userId", required = false) Long userId,
                               Model model) {
        if ("edit".equals(mode)) {
            User userToEdit = userService.getUserById(userId);
            User currentUser = userService.getCurrentUser();

            if(currentUser == null || (!currentUser.getId().equals(userToEdit.getId()) ||
                    currentUser.getRole() != Role.ADMIN)) {
                throw new AccessDeniedException("Current user can not edit this data");
            }

            userDTO = modelMapper.map(userToEdit, UserUpsertDTO.class);
            userDTO.setPassword(null);
            model.addAttribute("userDTO", userDTO);
        }
        return "register-form";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("userDTO") UserUpsertDTO userDTO,
                               BindingResult errors,
                               @RequestParam("file") MultipartFile file,
                               Model model) {
        if (isUserInvalidAndLoadModel(userDTO, errors, model) ||
                multipartFileHandleUtil.isMultipartFileImageInvalidAndLoadModel(file, model)) {
            return "register-form";
        }

        if(multipartFileHandleUtil.isMultipartFileImage(file)) {
            multipartFileHandleUtil.handleMultipartFile(file, "/images/" + userDTO.getArtistName());
            userDTO.setImageUrl("images/" + userDTO.getArtistName()+"/" + file.getOriginalFilename());
        }

        User user = getAndPopulateUser(userDTO);
        if(Strings.isBlank(user.getPassword())){
            model.addAttribute("pwdError","Password should not be empty");
            return "register-form";
        }
        userService.upsertUser(user);

        User currentUser = userService.getCurrentUser();
        if(currentUser != null && currentUser.getRole() == Role.ADMIN){
            return "users";
        }
        return "redirect:/login";
    }

    @NotNull
    private User getAndPopulateUser(UserUpsertDTO userDTO) {
        User user;
        User currentUser = userService.getCurrentUser();
        if (currentUser != null && userDTO.getId() != null) {
            if((!currentUser.getId().equals(userDTO.getId()) ||
                    currentUser.getRole() != Role.ADMIN)) {
                throw new AccessDeniedException("Current user can not edit this data");
            }
            user = editUserCase(userDTO, currentUser);
        } else {
            user = modelMapper.map(userDTO, User.class);
            if(Strings.isNotBlank(user.getPassword())) {
                PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
                user.setPassword(encoder.encode(user.getPassword()));
            }
        }
        if(user.getRole() == null){
            user.setRole(Role.USER);
        }
        user.setActive(true);
        return user;
    }

    private User editUserCase(UserUpsertDTO userDTO, User currentUser) {
        User user;
        if(currentUser.getRole() == Role.ADMIN) {
            user = userService.getUserById(userDTO.getId());
        }
        else if(userDTO.getId().equals(currentUser.getId())) {
            user = currentUser;
        }
        else {
            throw new AccessDeniedException("Current user can not edit this data");
        }
        String originalPassword = null;
        if(Strings.isBlank(userDTO.getPassword())){
            originalPassword = user.getPassword();
        }
        modelMapper.map(userDTO, user);
        if(originalPassword != null) {
            user.setPassword(originalPassword);
        } else {
            PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
            user.setPassword(encoder.encode(user.getPassword()));
        }
        return user;
    }

    private boolean isUserInvalidAndLoadModel(UserUpsertDTO userDTO, BindingResult errors, Model model) {
        Optional<User> userOptional;
        boolean isUserInvalid = errors.hasErrors();

        if (errors.getFieldError("username") == null && userDTOValidator.isUserUsernameUsed(userDTO.getId(), userDTO.getUsername())) {
            model.addAttribute("usernameError", String.format("Username %s already exists", userDTO.getUsername()));
            isUserInvalid = true;
        }

        if (errors.getFieldError("email") == null && userDTOValidator.isUserEmailUsed(userDTO.getId(), userDTO.getEmail())) {
            model.addAttribute("emailError", String.format("Email %s already exists", userDTO.getEmail()));
            isUserInvalid = true;
        }

        return isUserInvalid;
    }
}

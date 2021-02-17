package com.organizer.contest.remix.web.controller;

import com.organizer.contest.remix.enums.Role;
import com.organizer.contest.remix.models.User;
import com.organizer.contest.remix.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController extends RegisterController{
    @Autowired
    UserService userService;

    @GetMapping
    public String getAllUsers(Model model) {
        model.addAttribute("users",userService.getAllUsers());
        return "users";
    }

    @PostMapping(params = "delete")
    public String deleteUser(@RequestParam("delete")Long id, Model model){
        User userToDelete = userService.getUserById(id);
        if(userToDelete.getRole() != Role.ADMIN) {
            userService.deleteUserById(id);
        }
        return "redirect:/users";
    }

    @PostMapping(params = "deactivate")
    public String deactivateUser(@RequestParam("deactivate") Long id, Model model){
        User user = userService.getUserById(id);
        if(user.getRole() != Role.ADMIN) {
            user.setActive(false);
            userService.upsertUser(user);
        }
        return "redirect:/users";
    }

    @PostMapping(params = "activate")
    public String activateUser(@RequestParam("activate") Long id, Model model){
        User user = userService.getUserById(id);
        if(user.getRole() != Role.ADMIN) {
            user.setActive(true);
            userService.upsertUser(user);
        }
        return "redirect:/users";
    }
}

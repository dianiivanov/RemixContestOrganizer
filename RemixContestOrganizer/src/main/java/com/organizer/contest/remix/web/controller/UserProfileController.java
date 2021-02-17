package com.organizer.contest.remix.web.controller;

import com.organizer.contest.remix.dto.UserProfileInfoDTO;
import com.organizer.contest.remix.models.Submission;
import com.organizer.contest.remix.models.User;
import com.organizer.contest.remix.service.SubmissionService;
import com.organizer.contest.remix.service.UserService;
import com.organizer.contest.remix.web.util.MultipartFileHandleUtil;
import com.organizer.contest.remix.web.validators.UserDTOValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import javax.validation.Valid;

@Controller
@RequestMapping("/profile")
public class UserProfileController {
    private final String PROF_DIR = "profiles/";
    private final String CONT_APP_DIR= "contestApplications/";
    private final String CONT_DIR = "contests/";

    @Autowired
    private UserService userService;

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private UserDTOValidator userDTOValidator;

    @Autowired
    private MultipartFileHandleUtil multipartFileHandleUtil;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    @GetMapping
    public String getCurrentUserProfile(Model model) {
        User currentUser = userService.getCurrentUser();
        UserProfileInfoDTO userProfileInfoDTO = modelMapper.map(currentUser, UserProfileInfoDTO.class);
        model.addAttribute("userProfileDTO", userProfileInfoDTO);
        model.addAttribute("isAllowed",true);
        model.addAttribute("artistName",currentUser.getArtistName());
        currentUser.getSubmissions().size();
        model.addAttribute("submissions",currentUser.getSubmissions());
        return PROF_DIR + "profile";
    }

    @Transactional
    @GetMapping("/{id}")
    public String getUserProfile(@PathVariable("id")Long id,Model model) {
        User user = userService.getUserById(id);
        UserProfileInfoDTO userProfileInfoDTO = modelMapper.map(user, UserProfileInfoDTO.class);
        User currentUser = userService.getCurrentUser();
        model.addAttribute("isAllowed",
                currentUser != null && currentUser.getId().equals(user.getId()));
        model.addAttribute("userProfileDTO", userProfileInfoDTO);
        model.addAttribute("artistName",user.getArtistName());
        user.getSubmissions().size();
        model.addAttribute("submissions",user.getSubmissions());
        return PROF_DIR + "profile";
    }

    @GetMapping("/edit")
    public String getUserProfileEditForm(Model model) {
        User currentUser = userService.getCurrentUser();
        UserProfileInfoDTO userProfileInfoDTO = modelMapper.map(currentUser, UserProfileInfoDTO.class);
        model.addAttribute("userProfileDTO", userProfileInfoDTO);
        return PROF_DIR + "edit-profile";
    }

    @PostMapping("/edit")
    public String postUserProfileEditForm(@Valid UserProfileInfoDTO userProfileInfoDTO,
                                          BindingResult errors,
                                          @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                          Model model) {
        User currentUser = userService.getCurrentUser();
        if (multipartFileHandleUtil.isMultipartFileImageInvalidAndLoadModel(imageFile, model) ||
                isUserInvalidAndLoadModel(currentUser.getId(), userProfileInfoDTO.getEmail(), errors, model)) {
            return PROF_DIR + "edit-profile";
        }

        if(!multipartFileHandleUtil.isMultipartFileEmpty(imageFile)) {
            multipartFileHandleUtil.handleMultipartFile(imageFile, "/images/" + currentUser.getArtistName());
            userProfileInfoDTO.setImageUrl(imageFile.getOriginalFilename());
        }
        modelMapper.map(userProfileInfoDTO, currentUser);
        userService.upsertUser(currentUser);
        return "redirect:/" + PROF_DIR + "profile";
    }


    @Transactional
    @GetMapping("/contest-applications")
    public String getUserContestApplications(Model model) {
        User currentUser = userService.getCurrentUser();
        currentUser.getContestApplicationsOwned().size();
        model.addAttribute("contestApplicationsList", currentUser.getContestApplicationsOwned());
        return CONT_APP_DIR + "contest-applications-list";
    }

    @Transactional
    @GetMapping("/contests")
    public String getUserContests(Model model) {
        User currentUser = userService.getCurrentUser();
        currentUser.getContestsOwned().size();
        model.addAttribute("contests", currentUser.getContestsOwned());
        return CONT_DIR + "my-contests";
    }

    @PostMapping(params = "delete")
    public String deleteSubmission(@RequestParam("delete")Long id){
        submissionService.deleteSubmission(id);
        return "redirect:/profile";
    }

    private boolean isUserInvalidAndLoadModel(Long id, String email, BindingResult errors, Model model) {
        boolean isUserInvalid = errors.hasErrors();

        if (errors.getFieldError("email") == null && userDTOValidator.isUserEmailUsed(id, email)) {
            model.addAttribute("emailError", String.format("Email %s already exists", email));
            isUserInvalid = true;
        }

        return isUserInvalid;
    }
}

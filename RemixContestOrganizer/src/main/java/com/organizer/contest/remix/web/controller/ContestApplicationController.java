package com.organizer.contest.remix.web.controller;

import com.organizer.contest.remix.enums.ApplicationStatus;
import com.organizer.contest.remix.enums.Role;
import com.organizer.contest.remix.models.ContestApplication;
import com.organizer.contest.remix.models.User;
import com.organizer.contest.remix.service.ContestApplicationService;
import com.organizer.contest.remix.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@Controller
@RequestMapping("/contest-applications")
public class ContestApplicationController {
    private final String CONT_APP_DIR= "contestApplications/";

    @Autowired
    ContestApplicationService contestApplicationService;

    @Autowired
    UserService userService;

    @GetMapping
    public String getAllContestApplications(Model model) {
        List<ContestApplication> applicationList = contestApplicationService.getAllContestApplications();
        applicationList.forEach(application -> application.getId());
        model.addAttribute("contestApplicationsList",
                applicationList);
        return CONT_APP_DIR + "contest-applications-list";
    }

    @GetMapping("/{id}")
    public String getContestApplication(@PathVariable Long id, Model model) {
        User currentUser = userService.getCurrentUser();
        ContestApplication contestApplication = contestApplicationService.getContestApplicationById(id);
        if(currentUser.getRole()!= Role.ADMIN && !currentUser.getId().equals(contestApplication.getOwner().getId())){
            throw new AccessDeniedException("Current user can access this data");
        }
        model.addAttribute("contestApplication", contestApplicationService.getContestApplicationById(id));
        return CONT_APP_DIR + "contest-application";
    }

    @GetMapping("/form")
    public String getContestApplicationForm(@ModelAttribute("contestApplication") ContestApplication contestApplication,
                                            @RequestParam(value = "mode", required = false) String mode,
                                            @RequestParam(value = "contestApplicationId", required = false) Long contestApplicationId,
                                            Model model) {
        if ("edit".equals(mode)) {
            contestApplication = contestApplicationService.getContestApplicationById(contestApplicationId);
            model.addAttribute("contestApplication", contestApplication);
        }
        return CONT_APP_DIR + "contest-application-form";
    }

    @PostMapping("/form")
    public String upsertContestApplication(@Valid @ModelAttribute("contestApplication") ContestApplication application,
                                           BindingResult errors,
                                           Model model) {
        if (errors.hasErrors()) {
            return CONT_APP_DIR + "contest-application-form";
        }
        User currentUser = userService.getCurrentUser();
        if(application.getId() != null) {
            Long id = application.getId();
            ContestApplication applicationToEdit = contestApplicationService.getContestApplicationById(id);
            User applicationOwner = applicationToEdit.getOwner();
            if(currentUser == null || !applicationOwner.getId().equals(currentUser.getId())){
                throw new AccessDeniedException("Current user can not edit this data");
            }
            application.setContest(applicationToEdit.getContest());
        }

        application.setOwner(currentUser);

        ContestApplication contestApplication = contestApplicationService.saveContestApplication(application);
        return "redirect:/contest-applications/" + contestApplication.getId();
    }

    @PostMapping("/{id}/approve")
    public String approveContestApplication(@PathVariable Long id, Model model) {
        ContestApplication contestApplication = contestApplicationService.getContestApplicationById(id);
        contestApplication.setStatus(ApplicationStatus.APPROVED);
        contestApplication = contestApplicationService.saveContestApplication(contestApplication);
        return "redirect:/contest-applications/" + contestApplication.getId();
    }

    @PostMapping("/{id}/reject")
    public String rejectContestApplication(@PathVariable Long id, Model model) {
        ContestApplication contestApplication = contestApplicationService.getContestApplicationById(id);
        contestApplication.setStatus(ApplicationStatus.REJECTED);
        contestApplication = contestApplicationService.saveContestApplication(contestApplication);
        model.addAttribute("contestApplication", contestApplication);
        return "redirect:/contest-applications/" + contestApplication.getId();
    }
}

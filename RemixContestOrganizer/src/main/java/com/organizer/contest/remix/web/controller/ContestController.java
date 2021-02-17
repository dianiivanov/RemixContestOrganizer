package com.organizer.contest.remix.web.controller;

import com.organizer.contest.remix.enums.ContestStatus;
import com.organizer.contest.remix.enums.Role;
import com.organizer.contest.remix.exception.AlreadyExistingEntityException;
import com.organizer.contest.remix.models.*;
import com.organizer.contest.remix.service.*;
import com.organizer.contest.remix.web.util.MultipartFileHandleUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.comparator.NullSafeComparator;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/contests")
public class ContestController {
    private final String CONT_DIR = "contests/";

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    DBFileService DBFileService;

    @Autowired
    ContestService contestService;

    @Autowired
    ContestApplicationService contestApplicationService;

    @Autowired
    UserService userService;

    @Autowired
    private MultipartFileHandleUtil multipartFileHandleUtil;

    @GetMapping
    public String getAllContests(Model model){
        User currentUser = userService.getCurrentUser();
        if(currentUser != null && currentUser.getRole() == Role.ADMIN) {
            model.addAttribute("newContests", contestService.getByStatus(ContestStatus.NEW));
        }else {
            model.addAttribute("newContests", null);
        }
        model.addAttribute("runningContests", contestService.getByStatus(ContestStatus.RUNNING));
        model.addAttribute("judgingContests", contestService.getByStatus(ContestStatus.JUDGING));
        model.addAttribute("closedContests", contestService.getByStatus(ContestStatus.CLOSED));
        return CONT_DIR + "contests";
    }

    @Transactional
    @GetMapping("/{id}")
    public String getContest(@PathVariable Long id, Model model){
        Contest contest = contestService.getContestById(id);
        //contest.getSubmissions().size();

        User currentUser = userService.getCurrentUser();
        List<Submission> matchingSubmission = currentUser!=null?
                currentUser.getSubmissions().stream().filter(submission ->
                submission.getContest().getId().equals(contest.getId())).collect(Collectors.toList()):
                Collections.emptyList();
        model.addAttribute("submissionId", matchingSubmission.size() == 0 ? null :
                matchingSubmission.get(0).getId());
        contest.getStems().getId();

        Collections.sort(contest.getSubmissions(), new Comparator<Submission>() {
            @Override
            public int compare(Submission s1, Submission s2) {
                return NullSafeComparator.NULLS_HIGH.compare(s1.getPlaceInContest(), s2.getPlaceInContest());
            }
        });

        Boolean editableByCurrentUser = currentUser != null ?
                currentUser.getId().equals(contest.getOwner().getId()) :
                false;

        model.addAttribute("contest", contest);
        model.addAttribute("editableByCurrentUser", editableByCurrentUser);

        return CONT_DIR + "contest";
    }

    @GetMapping("/form")
    public String upsertContest(@ModelAttribute("contest") Contest contest,
                                @RequestParam(value = "mode", required = false) String mode,
                                @RequestParam(value = "contestId", required = false) Long contestId,
                                @RequestParam(value = "contestApplicationId", required = false) Long contestApplicationId,
                                Model model){
        User currentUser = userService.getCurrentUser();
        if("edit".equals(mode)) {
            contest = contestService.getContestById(contestId);
            if(currentUser == null || (currentUser.getRole() != Role.ADMIN &&
                    !currentUser.getId().equals(contest.getOwner().getId()))){
                throw new AccessDeniedException("Current user can not edit this data");
            }
            model.addAttribute("contest", contest);
        }
        model.addAttribute("contestApplicationId", contestApplicationId);
        return CONT_DIR + "contest-form";
    }

    @PostMapping("/form")
    public String createContest(@Valid @ModelAttribute("contest") Contest contest,
                                @RequestParam("contestApplicationId") Long contestApplicationId,
                                @RequestParam("imageFile") MultipartFile imageFile,
                                @RequestParam("stemsFile") MultipartFile stemsFile,
                                BindingResult errors,
                                Model model){
        if(errors.hasErrors()){
            return "contest-form";
        }
        ContestApplication contestApplication = contestApplicationService.getContestApplicationById(contestApplicationId);
        User currentUser = userService.getCurrentUser();
        if(contest.getId() != null && contestApplication.getContest() != null){
            Contest contestToEdit = contestService.getContestById(contest.getId());
            if(currentUser == null || (currentUser.getRole() != Role.ADMIN &&
                    !currentUser.getId().equals(contestToEdit.getOwner().getId()))){
                throw new AccessDeniedException("Current user can not edit this data");
            }
            contestToEdit.setDetails(contest.getDetails());
            contestToEdit.setPrizes(contest.getPrizes());
            contestToEdit.setRules(contest.getRules());
            contestToEdit.setStatus(contest.getStatus());
            contest = contestToEdit;
        }
        else{
            if(contestApplication.getContest() != null) {
                throw new AlreadyExistingEntityException("A contest for this application already exists.");
            }

            if(multipartFileHandleUtil.isMultipartFileEmpty(stemsFile)){
                model.addAttribute("stemsFileError", "Please provide stems for this contest");
                return "contest-form";
            }
            contest.setOwner(contestApplication.getOwner());
            contest.setContestApplication(contestApplication);
        }

        String contestFolderPath = getContestFolderPath(contest);

        if (!multipartFileHandleUtil.isMultipartFileEmpty(imageFile)) {
            multipartFileHandleUtil.handleMultipartFile(imageFile, contestFolderPath);
            contest.setCoverImageUrl(contestFolderPath + "/" + imageFile.getOriginalFilename());
        }

        Contest contestCreated = contestService.saveContest(contest);
        if(!multipartFileHandleUtil.isMultipartFileEmpty(stemsFile)) {
            DBFile createdStems = DBFileService.storeFile(stemsFile, contestCreated);
            contestCreated.setStems(createdStems);
            contestCreated = contestService.saveContest(contestCreated);
        }
        return "redirect:/contests/" + contestCreated.getId();
    }

    @PostMapping(path = "/{id}", params = "delete")
    public String deleteSubmission(@RequestParam("delete")Long id){
        Submission submission = submissionService.deleteSubmission(id);
        return "redirect:/contests/" + submission.getContest().getId();
    }

    private String getContestFolderPath(Contest contest) {
        return new StringBuilder()
                .append("contests/")
                .append(contest.getTitle())
                .toString();
    }
}

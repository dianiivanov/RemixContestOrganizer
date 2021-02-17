package com.organizer.contest.remix.web.controller;

import com.organizer.contest.remix.enums.ContestStatus;
import com.organizer.contest.remix.enums.Role;
import com.organizer.contest.remix.models.*;
import com.organizer.contest.remix.service.ContestService;
import com.organizer.contest.remix.service.SubmissionService;
import com.organizer.contest.remix.service.UserService;
import com.organizer.contest.remix.web.util.MultipartFileHandleUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import javax.validation.Valid;

@Controller
@RequestMapping("/submissions")
public class SubmissionController {
    private final String SUBMISSIONS_DIR = "submissions/";

    @Autowired
    SubmissionService submissionService;

    @Autowired
    ContestService contestService;

    @Autowired
    UserService userService;

    @Autowired
    private MultipartFileHandleUtil multipartFileHandleUtil;

    @GetMapping("/{id}")
    public String getSubmission(@PathVariable Long id, Model model) {
        User currentUser = userService.getCurrentUser();
        Submission submission = submissionService.getSubmissionById(id);
        Boolean editableByCurrentUser = currentUser != null ?
                currentUser.getId().equals(submission.getOwner().getId()) :
                false;
        Contest contest = submission.getContest();
        Boolean isAllowedToAnnounce = contest.getStatus() == ContestStatus.JUDGING && currentUser != null &&
                (currentUser.getRole() == Role.ADMIN ||
                        currentUser.getId().equals(contest.getOwner().getId()));
        model.addAttribute("submission", submission);
        model.addAttribute("isAllowedToAnnounce", isAllowedToAnnounce);
        model.addAttribute("editableByCurrentUser", editableByCurrentUser);
        model.addAttribute("contestId", submission.getContest().getId());
        return SUBMISSIONS_DIR + "submission";
    }

    @Transactional
    @GetMapping("/form")
    public String getContestSubmissionForm(@ModelAttribute("submission") Submission submission,
                                           @RequestParam(value = "submissionId", required = false) Long submissionId,
                                           @RequestParam(value = "contestId", required = false) Long contestId,
                                           @RequestParam(value = "mode", required = false) String mode,
                                           Model model) {
        User currentUser = userService.getCurrentUser();
        Contest contest = contestService.getContestById(contestId);

        if(contest.getOwner().getId().equals(currentUser.getId())) {
            throw new UnsupportedOperationException("You are the owner of this contest, you can not add a submission.");
        }
        //currentUser.getSubmissions().size();
        Boolean alreadyHasSubmission = currentUser.getSubmissions().stream().anyMatch(sub -> sub.getContest().getId().equals(contestId));
        if ("edit".equals(mode) || alreadyHasSubmission) {
            submission = submissionService.getSubmissionById(submissionId);
            if (!submission.getOwner().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("Current user can not edit this data");
            }
            model.addAttribute("submission", submission);
        }
        model.addAttribute("contestId", contestId);
        return SUBMISSIONS_DIR + "submission-form";
    }

    @Transactional
    @PostMapping("/form")
    public String postContestSubmission(@Valid @ModelAttribute("submission") Submission submission,
                                        @RequestParam(value = "contestId") Long contestId,
                                        BindingResult errors,
                                        @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                        @RequestParam(value = "songFile", required = false) MultipartFile songFile,
                                        Model model) {
        if (multipartFileHandleUtil.isMultipartFileImageInvalidAndLoadModel(imageFile, model) ||
                multipartFileHandleUtil.isMultipartFileAudioInvalidAndLoadModel(songFile, model)) {
            model.addAttribute("submission", submission);
            model.addAttribute("contestId", contestId);
            return SUBMISSIONS_DIR + "submission-form";
        }

        Contest contest = contestService.getContestById(contestId);
        if (submission.getId() != null) {
            Submission submissionToEdit = submissionService.getSubmissionById(submission.getId());
            populateSubmissionToEdit(submission, submissionToEdit);
            submission = submissionToEdit;
        } else {
            populateSubmissionToCreate(contest, submission);
        }

        String submissionFolderPath = getSubmissionFolderPath(submission);

        if (!multipartFileHandleUtil.isMultipartFileEmpty(imageFile)) {
            multipartFileHandleUtil.handleMultipartFile(imageFile, submissionFolderPath);
            submission.setCoverImageUrl(submissionFolderPath + "/" + imageFile.getOriginalFilename());
        } else {
            submission.setCoverImageUrl(contest.getCoverImageUrl());
        }

        if (!multipartFileHandleUtil.isMultipartFileEmpty(songFile)) {
            multipartFileHandleUtil.handleMultipartFile(songFile, submissionFolderPath);
            submission.setAudioFileUrl(submissionFolderPath + "/" + songFile.getOriginalFilename());
        }

        Submission submissionSaved = submissionService.saveSubmission(submission);
        return "redirect:/" + SUBMISSIONS_DIR + submissionSaved.getId();
    }

    @PostMapping("/announce")
    public String announceSubmission(@Valid @ModelAttribute("submission") Submission submission,
                                        BindingResult errors,
                                        Model model){
        Submission submissionToAnnounce = submissionService.getSubmissionById(submission.getId());
        Contest contest = submissionToAnnounce.getContest();
        User currentUser = userService.getCurrentUser();
        Boolean isAllowedToAnnounce = contest.getStatus() == ContestStatus.JUDGING &&
                (currentUser.getRole() == Role.ADMIN ||
                        currentUser.getId().equals(contest.getOwner().getId()));
        if(!isAllowedToAnnounce){
            throw new AccessDeniedException("You are not allowed to announce the winners");
        }
        submissionToAnnounce.setPlaceInContest(submission.getPlaceInContest());
        submissionService.saveSubmission(submissionToAnnounce);
        return "redirect:/" + SUBMISSIONS_DIR + submissionToAnnounce.getId();
    }

    private String getSubmissionFolderPath(Submission submission) {
        return new StringBuilder()
                .append("contests/")
                .append(submission.getContest().getTitle())
                .append("/submissions/")
                .append(submission.getOwner().getArtistName())
                .toString();
    }

    private String getContestFolderPath(Contest contest) {
        return new StringBuilder()
                .append("contests/")
                .append(contest.getTitle())
                .toString();
    }

    private void populateSubmissionToCreate(Contest contest, Submission submission) {
        if (contest.getStatus() != ContestStatus.RUNNING) {
            throw new UnsupportedOperationException("This contest is not running now.");
        }
        submission.setOwner(userService.getCurrentUser());
        submission.setContest(contest);
        submission.setTitle(contest.getTitle() + "(" + submission.getOwner().getArtistName() + " Remix)");
    }

    private void populateSubmissionToEdit(Submission submission, Submission submissionToEdit) {
        submissionToEdit.setDescription(submission.getDescription());
        submissionToEdit.setTempo(submission.getTempo());
        submissionToEdit.setTonality(submission.getTonality());
    }

}

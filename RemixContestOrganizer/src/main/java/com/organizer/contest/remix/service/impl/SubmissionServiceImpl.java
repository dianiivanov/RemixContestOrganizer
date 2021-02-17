package com.organizer.contest.remix.service.impl;

import com.organizer.contest.remix.exception.NonExistingEntityException;
import com.organizer.contest.remix.models.Submission;
import com.organizer.contest.remix.repository.SubmissionRepository;
import com.organizer.contest.remix.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubmissionServiceImpl implements SubmissionService {
    @Autowired
    private SubmissionRepository submissionRepository;

    @Override
    public Submission getSubmissionById(Long id) {
        return submissionRepository.findById(id).orElseThrow(() ->
                new NonExistingEntityException(String.format("There is not a contest with is=%s", id)));
    }

    @Override
    public Submission saveSubmission(Submission submission) {
        return submissionRepository.save(submission);
    }

    @Override
    public Submission deleteSubmission(Long id) {
        Submission submission = getSubmissionById(id);
        submissionRepository.delete(submission);
        return submission;
    }
}

package com.organizer.contest.remix.service;

import com.organizer.contest.remix.models.Submission;

public interface SubmissionService {
    Submission getSubmissionById(Long id);
    Submission saveSubmission(Submission submission);
    Submission deleteSubmission(Long submission);
}

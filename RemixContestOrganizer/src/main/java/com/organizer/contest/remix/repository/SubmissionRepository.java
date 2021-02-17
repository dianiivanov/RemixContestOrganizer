package com.organizer.contest.remix.repository;

import com.organizer.contest.remix.models.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
}

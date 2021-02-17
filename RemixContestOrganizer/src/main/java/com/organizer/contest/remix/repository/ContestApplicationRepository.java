package com.organizer.contest.remix.repository;

import com.organizer.contest.remix.models.ContestApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContestApplicationRepository extends JpaRepository<ContestApplication, Long> {
}

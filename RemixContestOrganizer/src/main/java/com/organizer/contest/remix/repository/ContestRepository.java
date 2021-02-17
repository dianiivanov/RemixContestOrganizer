package com.organizer.contest.remix.repository;

import com.organizer.contest.remix.enums.ContestStatus;
import com.organizer.contest.remix.models.Contest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContestRepository extends JpaRepository<Contest, Long> {
    List<Contest> findByStatus(ContestStatus status);
}

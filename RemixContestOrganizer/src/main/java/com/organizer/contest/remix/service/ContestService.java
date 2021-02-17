package com.organizer.contest.remix.service;

import com.organizer.contest.remix.enums.ContestStatus;
import com.organizer.contest.remix.models.Contest;

import java.util.List;

public interface ContestService {
    List<Contest> getAllContests();
    Contest getContestById(Long id);
    Contest saveContest(Contest contest);
    List<Contest> getByStatus(ContestStatus status);
}

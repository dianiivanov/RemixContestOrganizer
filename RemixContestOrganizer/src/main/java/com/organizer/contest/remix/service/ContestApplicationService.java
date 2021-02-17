package com.organizer.contest.remix.service;

import com.organizer.contest.remix.models.Contest;
import com.organizer.contest.remix.models.ContestApplication;

import java.util.List;
import java.util.Optional;

public interface ContestApplicationService {
    List<ContestApplication> getAllContestApplications();
    ContestApplication getContestApplicationById(Long id);
    ContestApplication saveContestApplication(ContestApplication application);
}

package com.organizer.contest.remix.service.impl;

import com.organizer.contest.remix.enums.ContestStatus;
import com.organizer.contest.remix.exception.NonExistingEntityException;
import com.organizer.contest.remix.models.Contest;
import com.organizer.contest.remix.repository.ContestRepository;
import com.organizer.contest.remix.service.ContestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContestServiceImpl implements ContestService {
    @Autowired
    private ContestRepository contestRepository;

    @Override
    public List<Contest> getAllContests() {
        return contestRepository.findAll();
    }

    @Override
    public Contest getContestById(Long id) {
        return contestRepository.findById(id).orElseThrow(() ->
                new NonExistingEntityException(String.format("There is not a contest with is=%s", id)));
    }

    @Override
    public Contest saveContest(Contest contest) {
        return contestRepository.save(contest);
    }

    @Override
    public List<Contest> getByStatus(ContestStatus status) {
        return contestRepository.findByStatus(status);
    }


}

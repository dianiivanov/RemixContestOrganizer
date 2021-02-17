package com.organizer.contest.remix.service.impl;

import com.organizer.contest.remix.exception.NonExistingEntityException;
import com.organizer.contest.remix.models.ContestApplication;
import com.organizer.contest.remix.repository.ContestApplicationRepository;
import com.organizer.contest.remix.service.ContestApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContestApplicationServiceImpl implements ContestApplicationService {
    @Autowired
    ContestApplicationRepository contestApplicationRepository;

    @Override
    public List<ContestApplication> getAllContestApplications() {
        return contestApplicationRepository.findAll();
    }

    @Override
    public ContestApplication getContestApplicationById(Long id) {
        return contestApplicationRepository.findById(id).orElseThrow(() -> new NonExistingEntityException(
                String.format("There is not contest application with id=%s",id)));
    }

    @Override
    public ContestApplication saveContestApplication(ContestApplication application) {
        return contestApplicationRepository.save(application);
    }
}

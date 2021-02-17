package com.organizer.contest.remix.service.impl;

import com.organizer.contest.remix.exception.FileStorageException;
import com.organizer.contest.remix.exception.NonExistingEntityException;
import com.organizer.contest.remix.models.Contest;
import com.organizer.contest.remix.models.DBFile;
import com.organizer.contest.remix.repository.DBFileRepository;
import com.organizer.contest.remix.service.DBFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class DBFileServiceImpl implements DBFileService {
    @Autowired
    private DBFileRepository DBFileRepository;

    @Override
    public DBFile storeFile(MultipartFile file, Contest contest) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try{
            if(fileName.contains("..")){
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            DBFile audioFile = new DBFile(fileName,file.getContentType(),file.getBytes());
            audioFile.setContest(contest);

            return DBFileRepository.save(audioFile);
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    @Override
    public DBFile getFileById(Long id) {
        return DBFileRepository.findById(id).orElseThrow(()->
                new NonExistingEntityException(String.format("Could not find archive with id = %s", id)));
    }

    @Override
    public DBFile saveAudioFile(DBFile dbFile) {
        return DBFileRepository.save(dbFile);
    }
}

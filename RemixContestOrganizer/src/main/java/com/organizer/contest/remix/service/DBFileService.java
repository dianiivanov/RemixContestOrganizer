package com.organizer.contest.remix.service;

import com.organizer.contest.remix.models.Contest;
import com.organizer.contest.remix.models.DBFile;
import org.springframework.web.multipart.MultipartFile;

public interface DBFileService {
    DBFile storeFile(MultipartFile file, Contest contest);
    DBFile getFileById(Long id);
    DBFile saveAudioFile(DBFile audioFile);
}

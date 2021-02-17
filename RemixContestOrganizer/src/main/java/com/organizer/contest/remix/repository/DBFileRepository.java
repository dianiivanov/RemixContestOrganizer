package com.organizer.contest.remix.repository;

import com.organizer.contest.remix.models.DBFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DBFileRepository extends JpaRepository<DBFile, Long> {
}

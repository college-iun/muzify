package com.muzify.repository;

import com.muzify.entity.ListeningHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ListeningHistoryRepository extends JpaRepository<ListeningHistory, Long> {
    List<ListeningHistory> findByUserIdOrderByStartedAtDesc(Long userId);
    long countByTrackId(Long trackId);
    void deleteByTrackId(Long trackId);
}

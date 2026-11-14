package com.muzify.repository;

import com.muzify.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserIdOrderByAddedAtDesc(Long userId);
    Optional<Favorite> findByUserIdAndTrackId(Long userId, Long trackId);
    boolean existsByUserIdAndTrackId(Long userId, Long trackId);
    long countByTrackId(Long trackId);
    void deleteByTrackId(Long trackId);
}

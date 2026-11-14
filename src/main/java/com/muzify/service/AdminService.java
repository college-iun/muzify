package com.muzify.service;

import com.muzify.dto.AdminStats;
import com.muzify.dto.ArtistTrackStats;
import com.muzify.dto.UserResponse;
import com.muzify.entity.Role;
import com.muzify.entity.User;
import com.muzify.exception.ApiException;
import com.muzify.repository.FavoriteRepository;
import com.muzify.repository.ListeningHistoryRepository;
import com.muzify.repository.TrackRepository;
import com.muzify.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository users;
    private final TrackRepository tracks;
    private final FavoriteRepository favorites;
    private final ListeningHistoryRepository history;

    public AdminService(UserRepository users, TrackRepository tracks,
                        FavoriteRepository favorites, ListeningHistoryRepository history) {
        this.users = users;
        this.tracks = tracks;
        this.favorites = favorites;
        this.history = history;
    }

    public List<UserResponse> allUsers() {
        return users.findAll().stream().map(UserResponse::from).toList();
    }

    @Transactional
    public UserResponse changeRole(Long userId, Role role, User current) {
        User u = users.findById(userId)
                .orElseThrow(() -> ApiException.notFound("Пользователь не найден"));
        if (u.getId().equals(current.getId())) {
            throw ApiException.badRequest("Нельзя изменить собственную роль");
        }
        u.setRole(role);
        return UserResponse.from(users.save(u));
    }

    public AdminStats stats() {
        return new AdminStats(users.count(), tracks.count(), history.count(), favorites.count());
    }

    /** Статистика по трекам, загруженным текущим исполнителем. */
    @Transactional(readOnly = true)
    public List<ArtistTrackStats> artistStats(User artist) {
        return tracks.findByCreatedById(artist.getId()).stream()
                .map(t -> new ArtistTrackStats(t.getId(), t.getTitle(),
                        history.countByTrackId(t.getId()), favorites.countByTrackId(t.getId())))
                .toList();
    }
}

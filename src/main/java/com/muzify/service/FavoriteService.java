package com.muzify.service;

import com.muzify.dto.ShortTrack;
import com.muzify.entity.Favorite;
import com.muzify.entity.Track;
import com.muzify.entity.User;
import com.muzify.exception.ApiException;
import com.muzify.repository.FavoriteRepository;
import com.muzify.repository.TrackRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FavoriteService {

    private final FavoriteRepository favorites;
    private final TrackRepository tracks;

    public FavoriteService(FavoriteRepository favorites, TrackRepository tracks) {
        this.favorites = favorites;
        this.tracks = tracks;
    }

    @Transactional(readOnly = true)
    public List<ShortTrack> list(User user) {
        return favorites.findByUserIdOrderByAddedAtDesc(user.getId()).stream()
                .map(f -> ShortTrack.from(f.getTrack()))
                .toList();
    }

    @Transactional
    public void add(Long trackId, User user) {
        Track track = tracks.findById(trackId)
                .orElseThrow(() -> ApiException.notFound("Трек не найден"));
        if (favorites.existsByUserIdAndTrackId(user.getId(), trackId)) {
            throw ApiException.conflict("Трек уже находится в избранном");
        }
        Favorite f = new Favorite();
        f.setUser(user);
        f.setTrack(track);
        favorites.save(f);
    }

    @Transactional
    public void remove(Long trackId, User user) {
        Favorite f = favorites.findByUserIdAndTrackId(user.getId(), trackId)
                .orElseThrow(() -> ApiException.notFound("Трек не найден в избранном"));
        favorites.delete(f);
    }
}

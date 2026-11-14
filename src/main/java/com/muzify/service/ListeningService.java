package com.muzify.service;

import com.muzify.dto.HistoryItem;
import com.muzify.dto.ListeningResponse;
import com.muzify.entity.ListeningHistory;
import com.muzify.entity.Track;
import com.muzify.entity.User;
import com.muzify.exception.ApiException;
import com.muzify.repository.ListeningHistoryRepository;
import com.muzify.repository.TrackRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ListeningService {

    private final ListeningHistoryRepository history;
    private final TrackRepository tracks;

    public ListeningService(ListeningHistoryRepository history, TrackRepository tracks) {
        this.history = history;
        this.tracks = tracks;
    }

    @Transactional
    public ListeningResponse start(Long trackId, User user) {
        Track track = tracks.findById(trackId)
                .orElseThrow(() -> ApiException.notFound("Трек не найден"));
        ListeningHistory h = new ListeningHistory();
        h.setUser(user);
        h.setTrack(track);
        h.setStartedAt(LocalDateTime.now().withNano(0));
        history.save(h);
        return new ListeningResponse(track.getId(), h.getStartedAt());
    }

    @Transactional(readOnly = true)
    public List<HistoryItem> history(User user) {
        return history.findByUserIdOrderByStartedAtDesc(user.getId()).stream()
                .map(h -> new HistoryItem(h.getTrack().getId(), h.getTrack().getTitle(),
                        h.getTrack().getArtist(), h.getStartedAt()))
                .toList();
    }
}

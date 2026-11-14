package com.muzify.service;

import com.muzify.dto.TrackRequest;
import com.muzify.dto.TrackResponse;
import com.muzify.entity.Role;
import com.muzify.entity.Track;
import com.muzify.entity.User;
import com.muzify.exception.ApiException;
import com.muzify.repository.FavoriteRepository;
import com.muzify.repository.ListeningHistoryRepository;
import com.muzify.repository.TrackRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrackService {

    private final TrackRepository tracks;
    private final FavoriteRepository favorites;
    private final ListeningHistoryRepository history;

    public TrackService(TrackRepository tracks, FavoriteRepository favorites,
                        ListeningHistoryRepository history) {
        this.tracks = tracks;
        this.favorites = favorites;
        this.history = history;
    }

    @Transactional(readOnly = true)
    public List<TrackResponse> search(String q) {
        List<Track> list = (q == null || q.isBlank())
                ? tracks.findAll()
                : tracks.findByTitleContainingIgnoreCaseOrArtistContainingIgnoreCase(q, q);
        return list.stream().map(TrackResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public TrackResponse get(Long id) {
        return TrackResponse.from(find(id));
    }

    @Transactional
    public TrackResponse create(TrackRequest req, User author) {
        Track t = new Track();
        apply(t, req);
        t.setCreatedBy(author);
        return TrackResponse.from(tracks.save(t));
    }

    @Transactional
    public TrackResponse update(Long id, TrackRequest req, User current) {
        Track t = find(id);
        checkOwner(t, current);
        apply(t, req);
        return TrackResponse.from(tracks.save(t));
    }

    @Transactional
    public void delete(Long id, User current) {
        Track t = find(id);
        checkOwner(t, current);
        favorites.deleteByTrackId(id);
        history.deleteByTrackId(id);
        tracks.delete(t);
    }

    private Track find(Long id) {
        return tracks.findById(id).orElseThrow(() -> ApiException.notFound("Трек не найден"));
    }

    private void apply(Track t, TrackRequest req) {
        t.setTitle(req.title());
        t.setArtist(req.artist());
        t.setGenre(req.genre());
        t.setDurationSeconds(req.durationSeconds());
    }

    /** Изменять трек может его автор (ARTIST) или администратор. */
    private void checkOwner(Track t, User current) {
        boolean admin = current.getRole() == Role.ADMIN;
        boolean owner = t.getCreatedBy() != null && t.getCreatedBy().getId().equals(current.getId());
        if (!admin && !owner) {
            throw ApiException.forbidden("Нельзя изменять чужой трек");
        }
    }
}

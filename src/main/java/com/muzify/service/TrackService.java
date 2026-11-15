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
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class TrackService {

    private final TrackRepository tracks;
    private final FavoriteRepository favorites;
    private final ListeningHistoryRepository history;
    private final StorageService storage;

    public TrackService(TrackRepository tracks, FavoriteRepository favorites,
                        ListeningHistoryRepository history, StorageService storage) {
        this.storage = storage;
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
        storage.delete(t.getAudioFile());
        tracks.delete(t);
    }

    /** Загрузка (или замена) аудиофайла трека автором или администратором. */
    @Transactional
    public TrackResponse uploadAudio(Long id, MultipartFile file, User current) {
        Track t = find(id);
        checkOwner(t, current);
        String name = storage.save(file);
        storage.delete(t.getAudioFile());
        t.setAudioFile(name);
        return TrackResponse.from(tracks.save(t));
    }

    /** Аудиофайл трека для воспроизведения. */
    @Transactional(readOnly = true)
    public Resource audio(Long id) {
        Track t = find(id);
        if (t.getAudioFile() == null) {
            throw ApiException.notFound("Для этого трека аудио не загружено");
        }
        return storage.load(t.getAudioFile());
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

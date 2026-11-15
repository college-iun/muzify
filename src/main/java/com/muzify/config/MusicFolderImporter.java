package com.muzify.config;

import com.muzify.entity.Track;
import com.muzify.repository.TrackRepository;
import com.muzify.service.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * При старте добавляет в каталог все аудиофайлы из папки music/ (app.music.dir).
 * Имя файла в формате "Исполнитель - Название.mp3"; если дефиса нет, исполнитель неизвестен.
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class MusicFolderImporter implements CommandLineRunner {

    private static final Set<String> EXT = Set.of("mp3", "wav", "ogg", "m4a");

    private final TrackRepository tracks;
    private final StorageService storage;
    private final Path dir;

    public MusicFolderImporter(TrackRepository tracks, StorageService storage,
                               @Value("${app.music.dir}") String dir) {
        this.tracks = tracks;
        this.storage = storage;
        this.dir = Paths.get(dir).toAbsolutePath().normalize();
    }

    @Override
    public void run(String... args) throws Exception {
        if (!Files.isDirectory(dir)) return;
        List<Path> files;
        try (Stream<Path> s = Files.list(dir)) {
            files = s.filter(Files::isRegularFile).sorted().collect(Collectors.toList());
        }
        for (Path p : files) {
            String file = p.getFileName().toString();
            int dot = file.lastIndexOf('.');
            if (dot <= 0) continue;
            String ext = file.substring(dot + 1).toLowerCase();
            if (!EXT.contains(ext)) continue;

            String base = file.substring(0, dot).trim();
            String artist = "Неизвестный исполнитель";
            String title = base;
            int sep = base.indexOf(" - ");
            if (sep > 0) {
                artist = base.substring(0, sep).trim();
                title = base.substring(sep + 3).trim();
            }
            if (title.isEmpty() || tracks.existsByTitleAndArtist(title, artist)) continue;

            Track t = new Track();
            t.setTitle(title);
            t.setArtist(artist);
            t.setGenre(null);
            try (InputStream in = Files.newInputStream(p)) {
                t.setAudioFile(storage.saveStream(in, "lib-" + Math.abs(file.hashCode()) + "." + ext));
            }
            tracks.save(t);
        }
    }
}

package com.muzify.config;

import com.muzify.entity.Role;
import com.muzify.entity.Track;
import com.muzify.entity.User;
import com.muzify.repository.TrackRepository;
import com.muzify.repository.UserRepository;
import com.muzify.service.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.io.InputStream;

/** Тестовые данные: 3 пользователя (по одному на роль) и 5 демо-треков с аудио. */
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seed(UserRepository users, TrackRepository tracks,
                           PasswordEncoder encoder, StorageService storage) {
        return args -> {
            if (users.count() > 0) return;

            users.save(user("admin", "admin@muzify.com", "admin123", Role.ADMIN, encoder));
            User artist = users.save(user("artist", "artist@muzify.com", "artist123", Role.ARTIST, encoder));
            users.save(user("user", "user@muzify.com", "user123", Role.USER, encoder));

            tracks.save(track("Midnight Drive", "The Neon Waves", "Synthwave", 24, artist, "midnight-drive.wav", storage));
            tracks.save(track("Morning Coffee", "The Neon Waves", "Lo-fi", 24, artist, "morning-coffee.wav", storage));
            tracks.save(track("Ocean Eyes", "Luna Park", "Pop", 24, null, "ocean-eyes.wav", storage));
            tracks.save(track("Electric Storm", "Thunder Road", "Rock", 24, null, "electric-storm.wav", storage));
            tracks.save(track("Night Train", "Blue Jazz Trio", "Jazz", 24, null, "night-train.wav", storage));
        };
    }

    private User user(String name, String email, String pass, Role role, PasswordEncoder enc) {
        User u = new User();
        u.setUsername(name);
        u.setEmail(email);
        u.setPassword(enc.encode(pass));
        u.setRole(role);
        return u;
    }

    private Track track(String title, String artist, String genre, int sec, User author,
                        String demoFile, StorageService storage) throws IOException {
        Track t = new Track();
        t.setTitle(title);
        t.setArtist(artist);
        t.setGenre(genre);
        t.setDurationSeconds(sec);
        t.setCreatedBy(author);
        try (InputStream in = new ClassPathResource("demo-audio/" + demoFile).getInputStream()) {
            t.setAudioFile(storage.saveStream(in, demoFile));
        }
        return t;
    }
}

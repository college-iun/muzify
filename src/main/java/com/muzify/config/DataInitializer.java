package com.muzify.config;

import com.muzify.entity.Role;
import com.muzify.entity.Track;
import com.muzify.entity.User;
import com.muzify.repository.TrackRepository;
import com.muzify.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Тестовые данные: 3 пользователя (по одному на роль) и несколько треков. */
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seed(UserRepository users, TrackRepository tracks, PasswordEncoder encoder) {
        return args -> {
            if (users.count() > 0) return;

            users.save(user("admin", "admin@muzify.com", "admin123", Role.ADMIN, encoder));
            User artist = users.save(user("artist", "artist@muzify.com", "artist123", Role.ARTIST, encoder));
            users.save(user("user", "user@muzify.com", "user123", Role.USER, encoder));

            tracks.save(track("Midnight Drive", "The Neon Waves", "Synthwave", 214, artist));
            tracks.save(track("Morning Coffee", "The Neon Waves", "Lo-fi", 187, artist));
            tracks.save(track("Ocean Eyes", "Luna Park", "Pop", 201, null));
            tracks.save(track("Bohemian Echo", "Queen Cover Band", "Rock", 355, null));
            tracks.save(track("Night Train", "Blue Jazz Trio", "Jazz", 262, null));
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

    private Track track(String title, String artist, String genre, int sec, User author) {
        Track t = new Track();
        t.setTitle(title);
        t.setArtist(artist);
        t.setGenre(genre);
        t.setDurationSeconds(sec);
        t.setCreatedBy(author);
        return t;
    }
}

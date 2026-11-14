package com.muzify.controller;

import com.muzify.dto.MessageResponse;
import com.muzify.dto.ShortTrack;
import com.muzify.entity.User;
import com.muzify.service.FavoriteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService service;

    public FavoriteController(FavoriteService service) { this.service = service; }

    @GetMapping
    public Map<String, List<ShortTrack>> list(@AuthenticationPrincipal User user) {
        return Map.of("favorites", service.list(user));
    }

    @PostMapping("/{trackId}")
    public ResponseEntity<MessageResponse> add(@PathVariable Long trackId,
                                               @AuthenticationPrincipal User user) {
        service.add(trackId, user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse("Трек добавлен в избранное"));
    }

    @DeleteMapping("/{trackId}")
    public ResponseEntity<Void> remove(@PathVariable Long trackId,
                                       @AuthenticationPrincipal User user) {
        service.remove(trackId, user);
        return ResponseEntity.noContent().build();
    }
}

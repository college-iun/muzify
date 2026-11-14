package com.muzify.controller;

import com.muzify.dto.TrackRequest;
import com.muzify.dto.TrackResponse;
import com.muzify.entity.User;
import com.muzify.service.TrackService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tracks")
public class TrackController {

    private final TrackService service;

    public TrackController(TrackService service) { this.service = service; }

    @GetMapping
    public Map<String, List<TrackResponse>> search(@RequestParam(required = false) String q) {
        return Map.of("tracks", service.search(q));
    }

    @GetMapping("/{id}")
    public TrackResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public ResponseEntity<TrackResponse> create(@Valid @RequestBody TrackRequest req,
                                                @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req, user));
    }

    @PutMapping("/{id}")
    public TrackResponse update(@PathVariable Long id, @Valid @RequestBody TrackRequest req,
                                @AuthenticationPrincipal User user) {
        return service.update(id, req, user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        service.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}

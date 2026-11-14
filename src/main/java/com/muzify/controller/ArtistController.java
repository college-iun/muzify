package com.muzify.controller;

import com.muzify.dto.ArtistTrackStats;
import com.muzify.entity.User;
import com.muzify.service.AdminService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/artist")
public class ArtistController {

    private final AdminService service;

    public ArtistController(AdminService service) { this.service = service; }

    /** Статистика прослушиваний и добавлений в избранное по трекам исполнителя. */
    @GetMapping("/stats")
    public Map<String, List<ArtistTrackStats>> stats(@AuthenticationPrincipal User user) {
        return Map.of("tracks", service.artistStats(user));
    }
}

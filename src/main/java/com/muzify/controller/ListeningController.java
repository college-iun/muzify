package com.muzify.controller;

import com.muzify.dto.HistoryItem;
import com.muzify.dto.ListeningRequest;
import com.muzify.dto.ListeningResponse;
import com.muzify.entity.User;
import com.muzify.service.ListeningService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/listening")
public class ListeningController {

    private final ListeningService service;

    public ListeningController(ListeningService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<ListeningResponse> start(@Valid @RequestBody ListeningRequest req,
                                                   @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.start(req.trackId(), user));
    }

    @GetMapping("/history")
    public Map<String, List<HistoryItem>> history(@AuthenticationPrincipal User user) {
        return Map.of("history", service.history(user));
    }
}

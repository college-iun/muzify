package com.muzify.dto;

import java.time.LocalDateTime;

public record ListeningResponse(Long trackId, LocalDateTime startedAt) { }

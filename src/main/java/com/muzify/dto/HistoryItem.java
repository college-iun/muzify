package com.muzify.dto;

import java.time.LocalDateTime;

public record HistoryItem(Long trackId, String title, String artist, LocalDateTime listenedAt) { }

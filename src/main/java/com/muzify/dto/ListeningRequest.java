package com.muzify.dto;

import jakarta.validation.constraints.NotNull;

public record ListeningRequest(@NotNull(message = "Не указан ID трека") Long trackId) { }

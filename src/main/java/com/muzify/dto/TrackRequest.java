package com.muzify.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record TrackRequest(
        @NotBlank(message = "Не указано название трека") String title,
        @NotBlank(message = "Не указан исполнитель") String artist,
        String genre,
        @Positive(message = "Длительность должна быть положительной") Integer durationSeconds
) { }

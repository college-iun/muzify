package com.muzify.dto;

import com.muzify.entity.Track;

public record TrackResponse(Long id, String title, String artist, String genre, Integer durationSeconds) {
    public static TrackResponse from(Track t) {
        return new TrackResponse(t.getId(), t.getTitle(), t.getArtist(), t.getGenre(), t.getDurationSeconds());
    }
}

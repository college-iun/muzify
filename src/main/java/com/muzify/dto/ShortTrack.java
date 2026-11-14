package com.muzify.dto;

import com.muzify.entity.Track;

/** Краткое представление трека: id, title, artist (формат из ТЗ). */
public record ShortTrack(Long id, String title, String artist) {
    public static ShortTrack from(Track t) {
        return new ShortTrack(t.getId(), t.getTitle(), t.getArtist());
    }
}

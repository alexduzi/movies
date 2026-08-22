package com.alexduzi.movies.dto.response;

import com.alexduzi.movies.entity.Movie;

public record MovieResponse(
        String title,
        Integer year,
        String studios,
        String producers,
        Boolean winner
) {
    public MovieResponse(Movie entity) {
        this(entity.getTitle(), entity.getYear(), entity.getStudios(), entity.getProducers(), entity.getWinner());
    }
}

package com.alexduzi.movies.controller;

import com.alexduzi.movies.dto.response.ProducerIntervalResponse;
import com.alexduzi.movies.entity.Movie;
import com.alexduzi.movies.service.MovieService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "${api.prefix}/movie")
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/")
    public ResponseEntity<List<Movie>> getAllMovies() {
        return ResponseEntity.ok(movieService.getMovies());
    }

    @GetMapping("/producer-intervals")
    public ResponseEntity<ProducerIntervalResponse> getProducerIntervals() {
        return ResponseEntity.ok(movieService.getProducerIntervals());
    }
}

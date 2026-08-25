package com.alexduzi.movies.service;

import com.alexduzi.movies.dto.response.IntervalDetails;
import com.alexduzi.movies.dto.response.ProducerIntervalResponse;
import com.alexduzi.movies.entity.Movie;
import com.alexduzi.movies.repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MovieService {
    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public ProducerIntervalResponse getProducerIntervals() {
        List<Movie> winners = movieRepository.findAllByWinnerTrueOrderByYearAsc();
        Map<String, Integer> lastWinYear = new HashMap<>();
        IntervalExtremesTracker tracker = new IntervalExtremesTracker();

        for (Movie movie : winners) {
            if (hasIncompleteData(movie)) {
                continue;
            }

            for (String producer : splitProducers(movie.getProducers())) {
                Integer previousYear = lastWinYear.get(producer);

                if (previousYear != null) {
                    int interval = movie.getYear() - previousYear;
                    tracker.register(new IntervalDetails(producer, interval, previousYear, movie.getYear()));
                }

                lastWinYear.put(producer, movie.getYear());
            }
        }

        return new ProducerIntervalResponse(tracker.getMinResults(), tracker.getMaxResults());
    }

    private boolean hasIncompleteData(Movie movie) {
        return movie.getYear() == null || movie.getProducers() == null || movie.getProducers().isBlank();
    }

    private List<String> splitProducers(String producers) {
        String normalized = producers
                .replace(", and ", ", ")
                .replace(" and ", ", ");

        return Arrays.stream(normalized.split(","))
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .toList();
    }
}

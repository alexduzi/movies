package com.alexduzi.movies.service;

import com.alexduzi.movies.dto.response.IntervalDetails;
import com.alexduzi.movies.dto.response.ProducerIntervalResponse;
import com.alexduzi.movies.entity.Movie;
import com.alexduzi.movies.repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<Movie> getMovies() {
        return movieRepository.findAll();
    }

    public ProducerIntervalResponse getProducerIntervals() {
        // encontrar todos os ganhadores com os anos ordenados
        List<Movie> winners = movieRepository.findAllByWinnerTrueOrderByYearAsc();

        Map<String, List<Integer>> producers = new HashMap<>();

        // criar um mapa dos ganhadores / anos
        for (Movie movie : winners) {
            if (movie.getYear() == null || movie.getProducers() == null || movie.getProducers().isBlank()) {
                continue;
            }

            for (String producerName : splitProducers(movie.getProducers())) {
                String cleanName = producerName.trim();
                if (cleanName.isEmpty()) continue;
                producers.computeIfAbsent(cleanName, k -> new ArrayList<>()).add(movie.getYear());
            }
        }

        List<IntervalDetails> allIntervals = new ArrayList<>();

        // calcular o intervalo de tempo entre um ano e outro
        for (Map.Entry<String, List<Integer>> entry : producers.entrySet()) {
            List<Integer> years = entry.getValue();
            if (years.size() < 2) continue;

            for (int i = 0; i < years.size() - 1; i++) {
                int previous = years.get(i);
                int following = years.get(i + 1);
                int interval = following - previous;

                allIntervals.add(new IntervalDetails(entry.getKey(), interval, previous, following));
            }
        }

        if (allIntervals.isEmpty()) {
            return new ProducerIntervalResponse();
        }

        // filtrar e retornar
        int minInterval = allIntervals.stream().mapToInt(IntervalDetails::interval).min().orElse(Integer.MAX_VALUE);
        int maxInterval = allIntervals.stream().mapToInt(IntervalDetails::interval).max().orElse(Integer.MIN_VALUE);

        List<IntervalDetails> minResults = allIntervals.stream()
                .filter(i -> i.interval() == minInterval)
                .toList();

        List<IntervalDetails> maxResults = allIntervals.stream()
                .filter(i -> i.interval() == maxInterval)
                .toList();

        return new ProducerIntervalResponse(minResults, maxResults);
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

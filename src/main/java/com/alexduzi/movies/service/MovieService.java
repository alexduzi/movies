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

    public ProducerIntervalResponse getProducerIntervals() {
        List<Movie> winners = movieRepository.findAllByWinnerTrueOrderByYearAsc();
        Map<String, List<Integer>> winsByProducer = mapWinYearsByProducer(winners);
        List<IntervalDetails> allIntervals = calculateIntervals(winsByProducer);

        if (allIntervals.isEmpty()) {
            return new ProducerIntervalResponse();
        }

        return buildMinMaxResponse(allIntervals);
    }

    private Map<String, List<Integer>> mapWinYearsByProducer(List<Movie> winners) {
        Map<String, List<Integer>> winsByProducer = new HashMap<>();
        for (Movie movie : winners) {
            if (movie.getYear() == null || movie.getProducers() == null || movie.getProducers().isBlank()) {
                continue;
            }

            for (String producerName : splitProducers(movie.getProducers())) {
                String cleanName = producerName.trim();
                if (cleanName.isEmpty()) continue;
                winsByProducer.computeIfAbsent(cleanName, k -> new ArrayList<>()).add(movie.getYear());
            }
        }
        return winsByProducer;
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

    private List<IntervalDetails> calculateIntervals(Map<String, List<Integer>> winsByProducer) {
        List<IntervalDetails> allIntervals = new ArrayList<>();
        for (Map.Entry<String, List<Integer>> entry : winsByProducer.entrySet()) {
            List<Integer> years = entry.getValue();
            if (years.size() < 2) continue;

            for (int i = 0; i < years.size() - 1; i++) {
                int previous = years.get(i);
                int following = years.get(i + 1);
                int interval = following - previous;

                allIntervals.add(new IntervalDetails(entry.getKey(), interval, previous, following));
            }
        }
        return allIntervals;
    }

    private ProducerIntervalResponse buildMinMaxResponse(List<IntervalDetails> allIntervals) {
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
}

package com.alexduzi.movies.dataloader;

import com.alexduzi.movies.entity.Movie;
import com.alexduzi.movies.repository.MovieRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class MovieDataLoader implements CommandLineRunner {
    private final MovieRepository movieRepository;

    public MovieDataLoader(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        ClassPathResource resource = new ClassPathResource("movielist.csv");

        if (!resource.exists()) {
            System.out.println("No movielist.csv to read!");
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            boolean isHeader = true;
            List<Movie> moviesToSave = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] columns = line.split(";", -1);

                if (columns.length >= 4) {
                    Movie movie = new Movie();

                    String yearStr = columns[0].trim();
                    movie.setYear(yearStr.isEmpty() ? null : Integer.parseInt(yearStr));

                    movie.setTitle(columns[1].trim());
                    movie.setStudios(columns[2].trim());
                    movie.setProducers(columns[3].trim());

                    if (columns.length > 4) {
                        String winnerStr = columns[4].trim();
                        movie.setWinner("yes".equalsIgnoreCase(winnerStr));
                    } else {
                        movie.setWinner(false);
                    }

                    moviesToSave.add(movie);
                }
            }
            movieRepository.saveAll(moviesToSave);
            System.out.println("Database successfully initialized for Golden Raspberry Awards API");
        }
    }
}

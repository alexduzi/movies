package com.alexduzi.movies.repository;

import com.alexduzi.movies.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findAllByWinnerTrue();
    List<Movie> findAllByWinnerTrueOrderByYearAsc();
}

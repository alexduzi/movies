package com.alexduzi.movies.controller;

import com.alexduzi.movies.entity.Movie;
import com.alexduzi.movies.repository.MovieRepository;
import com.jayway.jsonpath.JsonPath;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class MovieControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MovieRepository movieRepository;

    @Test
    void shouldReturnNonEmptyMinAndMaxLists() throws Exception {
        mockMvc.perform(get("/api/v1/movie/producer-intervals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.min").isNotEmpty())
                .andExpect(jsonPath("$.max").isNotEmpty());
    }

    @Test
    void shouldCalculateProducerIntervalsSuccessfullyWithAnyDataset() throws Exception {
        mockMvc.perform(get("/api/v1/movie/producer-intervals")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.min").isArray())
                .andExpect(jsonPath("$.max").isArray())
                .andExpect(jsonPath("$.min[0].producer").exists())
                .andExpect(jsonPath("$.min[0].interval").isNumber())
                .andExpect(jsonPath("$.min[0].previousWin").isNumber())
                .andExpect(jsonPath("$.min[0].followingWin").isNumber())
                .andExpect(jsonPath("$.max[0].producer").exists())
                .andExpect(jsonPath("$.max[0].interval").isNumber())
                .andExpect(jsonPath("$.max[0].previousWin").isNumber())
                .andExpect(jsonPath("$.max[0].followingWin").isNumber());
    }

    @Test
    void shouldHaveMinIntervalLessThanOrEqualToMax() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1/movie/producer-intervals")
                                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        int minInterval = JsonPath.read(json, "$.min[0].interval");
        int maxInterval = JsonPath.read(json, "$.max[0].interval");

        assertTrue(minInterval <= maxInterval);
    }

    @Test
    void shouldHaveFollowingWinGreaterThanPreviousWin() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1/movie/producer-intervals")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        int minPrevious = JsonPath.read(json, "$.min[0].previousWin");
        int minFollowing = JsonPath.read(json, "$.min[0].followingWin");

        assertTrue(minFollowing > minPrevious);
    }

    @Test
    void shouldHaveCorrectIntervalCalculation() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1/movie/producer-intervals")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        int minPrevious = JsonPath.read(json, "$.min[0].previousWin");
        int minFollowing = JsonPath.read(json, "$.min[0].followingWin");
        int minInterval = JsonPath.read(json, "$.min[0].interval");

        int maxPrevious = JsonPath.read(json, "$.max[0].previousWin");
        int maxFollowing = JsonPath.read(json, "$.max[0].followingWin");
        int maxInterval = JsonPath.read(json, "$.max[0].interval");

        assertEquals(minFollowing - minPrevious, minInterval);
        assertEquals(maxFollowing - maxPrevious, maxInterval);
    }

    @Test
    void shouldHavePositiveIntervals() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1/movie/producer-intervals"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        int minInterval = JsonPath.read(json, "$.min[0].interval");
        int maxInterval = JsonPath.read(json, "$.max[0].interval");

        assertTrue(minInterval > 0);
        assertTrue(maxInterval > 0);
    }

    @Test
    @Transactional
    void shouldReturnAllProducersTiedForMinAndMax() throws Exception {
        movieRepository.deleteAll();

        saveWinner("Producer A", 1990);
        saveWinner("Producer A", 1991);

        saveWinner("Producer B", 2000);
        saveWinner("Producer B", 2001);

        saveWinner("Producer C", 1960);
        saveWinner("Producer C", 1970);

        saveWinner("Producer D", 1980);
        saveWinner("Producer D", 1990);

        mockMvc.perform(get("/api/v1/movie/producer-intervals")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.min.length()").value(2))
                .andExpect(jsonPath("$.min[*].producer", containsInAnyOrder("Producer A", "Producer B")))
                .andExpect(jsonPath("$.min[*].interval", everyItem(is(1))))
                .andExpect(jsonPath("$.max.length()").value(2))
                .andExpect(jsonPath("$.max[*].producer", containsInAnyOrder("Producer C", "Producer D")))
                .andExpect(jsonPath("$.max[*].interval", everyItem(is(10))));
    }

    @Test
    void shouldMatchExpectedValuesFromDefaultDataset() throws Exception {
        mockMvc.perform(get("/api/v1/movie/producer-intervals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.min.length()").value(1))
                .andExpect(jsonPath("$.min[0].producer").value("Joel Silver"))
                .andExpect(jsonPath("$.min[0].interval").value(1))
                .andExpect(jsonPath("$.min[0].previousWin").value(1990))
                .andExpect(jsonPath("$.min[0].followingWin").value(1991))
                .andExpect(jsonPath("$.max.length()").value(1))
                .andExpect(jsonPath("$.max[0].producer").value("Matthew Vaughn"))
                .andExpect(jsonPath("$.max[0].interval").value(13))
                .andExpect(jsonPath("$.max[0].previousWin").value(2002))
                .andExpect(jsonPath("$.max[0].followingWin").value(2015));
    }

    private void saveWinner(String producer, int year) {
        Movie movie = new Movie();
        movie.setYear(year);
        movie.setTitle("Test Movie " + producer + " " + year);
        movie.setStudios("Test Studios");
        movie.setProducers(producer);
        movie.setWinner(true);
        movieRepository.save(movie);
    }
}

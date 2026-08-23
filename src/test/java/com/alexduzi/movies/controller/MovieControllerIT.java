package com.alexduzi.movies.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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
}

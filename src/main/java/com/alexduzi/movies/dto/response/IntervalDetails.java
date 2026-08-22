package com.alexduzi.movies.dto.response;

public record IntervalDetails(
        String producer,
        Integer interval,
        Integer previousWin,
        Integer followingWin
) { }

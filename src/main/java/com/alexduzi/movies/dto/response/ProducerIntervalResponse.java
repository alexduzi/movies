package com.alexduzi.movies.dto.response;

import java.util.ArrayList;
import java.util.List;

public record ProducerIntervalResponse(
        List<IntervalDetails> min,
        List<IntervalDetails> max
) {
    public ProducerIntervalResponse() {
        this(new ArrayList<>(), new ArrayList<>());
    }
}

package com.alexduzi.movies.service;

import com.alexduzi.movies.dto.response.IntervalDetails;

import java.util.ArrayList;
import java.util.List;

class IntervalExtremesTracker {
    private int minInterval = Integer.MAX_VALUE;
    private int maxInterval = Integer.MIN_VALUE;
    private final List<IntervalDetails> minResults = new ArrayList<>();
    private final List<IntervalDetails> maxResults = new ArrayList<>();

    public IntervalExtremesTracker() { }

    List<IntervalDetails> getMinResults() {
        return minResults;
    }

    List<IntervalDetails> getMaxResults() {
        return maxResults;
    }

    void register(IntervalDetails detail) {
        int interval = detail.interval();

        if (interval < minInterval) {
            minInterval = interval;
            minResults.clear();
            minResults.add(detail);
        } else if (interval == minInterval) {
            minResults.add(detail);
        }

        if (interval > maxInterval) {
            maxInterval = interval;
            maxResults.clear();
            maxResults.add(detail);
        } else if (interval == maxInterval) {
            maxResults.add(detail);
        }
    }
}

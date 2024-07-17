package org.example.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NewsResponse {
    private List<News> results;

    public NewsResponse() {
        // Default constructor for Jackson
    }

    public NewsResponse(List<News> results) {
        this.results = results;
    }

    // Getters and setters
    public List<News> getResults() {
        return results;
    }

    public void setResults(List<News> results) {
        this.results = results;
    }
}

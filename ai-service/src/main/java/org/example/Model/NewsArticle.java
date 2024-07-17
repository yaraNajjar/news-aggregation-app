package org.example.Model;

public class NewsArticle {
    private final String title;
    private final String url;

    public NewsArticle(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }
}
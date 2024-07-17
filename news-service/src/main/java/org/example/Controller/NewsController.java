package org.example.Controller;

import org.example.Service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/news")
public class NewsController {

    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    private NewsService newsService;

    @PostMapping("/fetch-news")
    public ResponseEntity<String> fetchNews(@RequestParam String userId) {
        logger.info("Received request to fetch news for userId: {}", userId);
        newsService.fetchAndProcessNews(userId);
        return ResponseEntity.accepted().body("News processing started");
    }
}

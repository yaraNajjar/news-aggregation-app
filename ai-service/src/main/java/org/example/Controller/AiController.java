package org.example.Controller;

import org.example.Service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Autowired
    private AiService aiService;

    /**
     * Endpoint to pick interesting news based on user preferences.
     *
     * @param payload The request payload containing news and preferences.
     * @return A string containing the filtered news articles.
     */
    @PostMapping("/pickInterestingNews")
    public String pickInterestingNews(@RequestBody Map<String, String> payload) {
        try {
            String news = payload.get("news");
            String preferences = payload.get("preferences");
            return aiService.pickInterestingNews(news, preferences);
        } catch (Exception e) {
            // Handle any exception that might occur
            return "Error processing request: " + e.getMessage();
        }
    }

    /**
     * Endpoint to generate a summary for a given news article.
     *
     * @param news The news article to summarize.
     * @return A string containing the summary.
     */
    @PostMapping("/generateSummary")
    public String generateSummary(@RequestBody String news) {
        try {
            return aiService.generateSummary(news);
        } catch (Exception e) {
            // Handle any exception that might occur
            return "Error processing request: " + e.getMessage();
        }
    }
}

package org.example.Service;

import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import io.dapr.client.domain.HttpExtension;
import org.example.Model.NewsResponse;
import org.example.Model.Preferences;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class NewsService {

    private static final Logger logger = LoggerFactory.getLogger(NewsService.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${newsdata.api.key}")
    private String apiKey;

    @Value("${newsdata.base.url}")
    private String baseUrl;

    @Value("${ai.service.url}")
    private String aiServiceUrl;

    @Value("${ai.generate.summary.url}")
    private String aiGenerateSummaryUrl;

    @Value("${user.preferences.url}")
    private String userPreferencesUrl;

    private final DaprClient daprClient = new DaprClientBuilder().build();

    /**
     * Fetches user preferences, fetches news based on those preferences,
     * picks interesting news using AI service, generates a summary for the news,
     * and sends the summary to a Kafka topic.
     *
     * @param userId The ID of the user to fetch preferences for.
     */
    public void fetchAndProcessNews(String userId) {
        logger.info("Fetching user preferences for userId: {}", userId);
        try {
            // Fetch user preferences
            String preferencesUrl = String.format("%s/users/preferences/%s", userPreferencesUrl, userId);
            Preferences preferences = daprClient.invokeMethod("user-service", preferencesUrl, null, HttpExtension.GET, Preferences.class).block();
            if (preferences == null) {
                logger.warn("No preferences found for userId: {}", userId);
                return;
            }

            logger.info("Fetching news based on user preferences");
            // Fetch news based on preferences
            String news = fetchNews(preferences);
            logger.debug("Fetched news: {}", news);

            logger.info("Picking interesting news");
            // Pick interesting news
            String interestingNews = pickInterestingNews(news, preferences);
            logger.debug("Interesting news: {}", interestingNews);

            // If no interesting news is found, select a random news item
            if (interestingNews == null || interestingNews.isEmpty()) {
                logger.warn("No interesting news found, selecting a random news item");
                String[] newsItems = news.split("\n");
                Random random = new Random();
                interestingNews = newsItems[random.nextInt(newsItems.length)];
            }

            logger.info("Generating summary for the selected news");
            // Generate concise summary
            String summary = generateSummary(interestingNews);
            logger.debug("Generated summary: {}", summary);

            logger.info("Sending the summary to Kafka");
            // Send the summary to Kafka
            kafkaTemplate.send("news-summaries", userId + ":" + summary);
        } catch (Exception e) {
            logger.error("Error in fetchAndProcessNews: {}", e.getMessage(), e);
        }
    }

    /**
     * Fetches news articles based on user preferences.
     *
     * @param preferences The user preferences.
     * @return A string containing the news articles.
     */
    public String fetchNews(Preferences preferences) {
        String categories = String.join(",", preferences.getCategories());
        String url = baseUrl + "?apikey=" + apiKey + "&q=" + categories;
        logger.debug("Fetching news from URL: {}", url);

        try {
            // Fetch news from the external API
            NewsResponse response = restTemplate.getForObject(url, NewsResponse.class);
            if (response != null && response.getResults() != null) {
                StringBuilder newsBuilder = new StringBuilder("Latest news based on preferences: ");
                response.getResults().forEach(news -> newsBuilder.append("Title: ").append(news.getTitle()).append(", Link: ").append(news.getLink()).append("\n"));
                return newsBuilder.toString();
            } else {
                return "No news found for the given preferences.";
            }
        } catch (Exception e) {
            logger.error("Error fetching news: {}", e.getMessage(), e);
            return "Error fetching news: " + e.getMessage();
        }
    }

    /**
     * Picks interesting news articles using an AI service.
     *
     * @param news        The news articles to analyze.
     * @param preferences The user preferences.
     * @return A string containing the interesting news article.
     */
    private String pickInterestingNews(String news, Preferences preferences) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("news", news);
        requestBody.put("preferences", String.join(",", preferences.getCategories()));

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            // Send a request to the AI service to pick interesting news
            ResponseEntity<String> response = restTemplate.postForEntity(aiServiceUrl, requestEntity, String.class);
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error fetching AI response: {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            return "Error fetching AI response: " + e.getStatusCode() + " " + e.getResponseBodyAsString();
        } catch (Exception e) {
            logger.error("Error fetching AI response: {}", e.getMessage(), e);
            return "Error fetching AI response: " + e.getMessage();
        }
    }

    /**
     * Generates a concise summary for a given news article using an AI service.
     *
     * @param news The news article to summarize.
     * @return A string containing the summary.
     */
    private String generateSummary(String news) {
        System.out.println("news: "+news);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("news", news);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            // Send a request to the AI service to generate a summary
            ResponseEntity<String> response = restTemplate.postForEntity(aiGenerateSummaryUrl, requestEntity, String.class);
            System.out.println("response: "+response.getBody());
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error generating summary: {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            return "Error generating summary: " + e.getStatusCode() + " " + e.getResponseBodyAsString();
        } catch (Exception e) {
            logger.error("Error generating summary: {}", e.getMessage(), e);
            return "Error generating summary: " + e.getMessage();
        }
    }
}

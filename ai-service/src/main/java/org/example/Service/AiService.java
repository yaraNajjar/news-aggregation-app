package org.example.Service;

import org.example.Model.NewsArticle;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.io.IOException;

@Service
public class AiService {

    private static final Logger logger = LoggerFactory.getLogger(AiService.class);

    @Value("${hugging.face.api.url}")
    private String huggingFaceApiUrl;

    @Value("${hugging.face.api.key}")
    private String huggingFaceApiKey;

    /**
     * Picks interesting news based on user preferences.
     *
     * @param news        The news articles to analyze.
     * @param preferences The user preferences.
     * @return A string containing the filtered news articles.
     */
    public String pickInterestingNews(String news, String preferences) {
        logger.info("Picking interesting news based on preferences");

        // Split the news into individual items using newline as a delimiter
        String[] newsItems = news.split("\n");

        // Convert preferences string to a list
        List<String> preferenceList = List.of(preferences.split(",")).stream()
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        logger.debug("News items: {}", (Object) newsItems);
        logger.debug("Preferences: {}", preferenceList);

        // Filter the news items based on preferences
        List<String> filteredNews = List.of(newsItems).stream()
                .filter(item -> {
                    String lowerCaseItem = item.toLowerCase();
                    return preferenceList.stream().anyMatch(lowerCaseItem::contains);
                })
                .collect(Collectors.toList());

        // Join filtered news items into a single string
        String results = String.join("\n", filteredNews);
        logger.debug("Filtered results: {}", results);

        return results;
    }

    /**
     * Generates a summary for a given news article.
     *
     * @param newsJson The news article to summarize.
     * @return A string containing the summary or an error message.
     */
    public String generateSummary(String newsJson) {
        logger.info("Generating summary using TextRazor for news: {}", newsJson);

        List<NewsArticle> articles = extractUrlsAndTitlesFromNews(newsJson);

        if (articles.isEmpty()) {
            logger.warn("No URL found in the news: {}", newsJson);
            return "No summary available";
        }

        StringBuilder summaries = new StringBuilder();
        for (NewsArticle article : articles) {
            try {
                logger.debug("Processing article URL: {}", article.getUrl());
                String articleContent = ArticleExtractor.getArticleContent(article.getUrl());

                if (articleContent == null || articleContent.isEmpty()) {
                    logger.warn("Failed to fetch article content from URL: {}", article.getUrl());
                    summaries.append("Failed to fetch article content for: ").append(article.getTitle()).append("\n");
                    continue;
                }

                // Generate summary using TextSummarizer
                String summary = TextSummarizer.summarizeText(articleContent);
                logger.debug("Generated summary for {}: {}", article.getTitle(), summary);
                summaries.append("Title: ").append(article.getTitle()).append("\nSummary: ").append(summary).append("\n\n");

            } catch (IOException e) {
                logger.warn("Failed to fetch article content from URL: {}", article.getUrl(), e);
                summaries.append("No summary available for: ").append(article.getTitle()).append(" due to HTTP error.\n");
            } catch (Exception e) {
                logger.error("Error generating summary: {}", e.getMessage(), e);
                summaries.append("Error generating summary for: ").append(article.getTitle()).append(" - ").append(e.getMessage()).append("\n");
            }
        }

        return summaries.toString();
    }

    public List<NewsArticle> extractUrlsAndTitlesFromNews(String newsJson) {
        List<NewsArticle> articles = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(newsJson);
            String news = jsonObject.getString("news");

            // Split the news string by occurrences of "Title: "
            String[] parts = news.split("Title: ");
            for (String part : parts) {
                if (part.trim().isEmpty()) continue; // Skip any empty parts

                // Extract the title
                int linkIndex = part.indexOf("Link: ");
                if (linkIndex == -1) continue; // Skip if no link found

                String title = part.substring(0, linkIndex - 2).trim();
                String urlPart = part.substring(linkIndex + 6).trim();

                // Handle multiple URLs
                String[] urls = urlPart.split("\\s+");
                for (String url : urls) {
                    articles.add(new NewsArticle(title, url));
                }
            }
        } catch (Exception e) {
            logger.error("Error extracting URLs and titles from news JSON.", e);
        }
        return articles;
    }
}

package org.example.Service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ArticleExtractor {

    private static final Logger logger = LoggerFactory.getLogger(ArticleExtractor.class);

    /**
     * Fetches and extracts the main content from the given URL.
     *
     * @param url The URL of the article to fetch.
     * @return The main content of the article as a string.
     * @throws IOException If an error occurs while fetching the content.
     */
    public static String getArticleContent(String url) throws IOException {
        logger.info("Fetching article content from URL: {}", url);

        try {
            // Fetch the HTML content from the given URL
            Document document = Jsoup.connect(url).get();

            // Extract the main content from the article
            Elements paragraphs = document.select("p");

            StringBuilder articleContent = new StringBuilder();
            for (Element paragraph : paragraphs) {
                articleContent.append(paragraph.text()).append("\n");
            }

            logger.debug("Successfully fetched article content from URL: {}", url);
            return articleContent.toString().trim();
        } catch (IOException e) {
            logger.error("Failed to fetch article content from URL: {}", url, e);
            throw e;
        }
    }
}

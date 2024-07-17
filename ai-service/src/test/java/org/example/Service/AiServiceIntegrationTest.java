package org.example.Service;

import org.example.Model.NewsArticle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class AiServiceIntegrationTest {

    @Autowired
    private AiService aiService;

    @Test
    public void testGenerateSummary() {
        // Example news JSON with valid URLs
        String newsJson = "{ \"news\": \"Title: OpenAI Startup Fund & Arianna Huffington's Thrive Global Create New Company, Thrive AI Health, To Launch Hyper-Personalized AI Health Coach, Link: https://www.prnewswire.co.uk/news-releases/openai-startup-fund--arianna-huffingtons-thrive-global-create-new-company-thrive-ai-health-to-launch-hyper-personalized-ai-health-coach-302190603.html\\nTitle: OpenAI Startup Fund & Arianna Huffington's Thrive Global Create New Company, Thrive AI Health, To Launch Hyper-Personalized AI Health Coach, Link: https://www.wvnews.com/news/around_the_web/partners/pr_newswire/subject/new_products_services/openai-startup-fund-arianna-huffingtons-thrive-global-create-new-company-thrive-ai-health-to-launch/article_a1a497e6-4309-56e2-89c1-cf424aa8bc65.html\\nTitle: Hardware Security Modules market is expected to be worth USD 1.27 billion. Hardware Security Modules Market is expected to grow at a 11.8 percentage, Link: https://www.openpr.com/news/3569666hardware-security-modules-market-is-expected-to-be-worth-usd-1-27\\nTitle: 'I Will Take My Business To Costco' - Sam's Club Members Threaten Because Major Perk Ends On Aug.19, Link: https://www.ibtimes.co.uki-will-take-my-business-costco-sams-club-members-threaten-because-major-perk-ends-aug-19-17252992\" }";
        String summary = aiService.generateSummary(newsJson);

        assertNotNull(summary);
        assertFalse(summary.isEmpty());
    }

    @Test
    public void testExtractUrlsAndTitlesFromNews() {
        // Example news JSON with valid URLs
        String newsJson = "{ \"news\": \"Title: OpenAI Startup Fund & Arianna Huffington's Thrive Global Create New Company, Thrive AI Health, To Launch Hyper-Personalized AI Health Coach, Link: https://www.prnewswire.co.uk/news-releases/openai-startup-fund--arianna-huffingtons-thrive-global-create-new-company-thrive-ai-health-to-launch-hyper-personalized-ai-health-coach-302190603.html\\nTitle: OpenAI Startup Fund & Arianna Huffington's Thrive Global Create New Company, Thrive AI Health, To Launch Hyper-Personalized AI Health Coach, Link: https://www.wvnews.com/news/around_the_web/partners/pr_newswire/subject/new_products_services/openai-startup-fund-arianna-huffingtons-thrive-global-create-new-company-thrive-ai-health-to-launch/article_a1a497e6-4309-56e2-89c1-cf424aa8bc65.html\\nTitle: Hardware Security Modules market is expected to be worth USD 1.27 billion. Hardware Security Modules Market is expected to grow at a 11.8 percentage, Link: https://www.openpr.com/news/3569666hardware-security-modules-market-is-expected-to-be-worth-usd-1-27\\nTitle: 'I Will Take My Business To Costco' - Sam's Club Members Threaten Because Major Perk Ends On Aug.19, Link: https://www.ibtimes.co.uki-will-take-my-business-costco-sams-club-members-threaten-because-major-perk-ends-aug-19-17252992\" }";
        List<NewsArticle> articles = aiService.extractUrlsAndTitlesFromNews(newsJson);

        assertNotNull(articles);
        assertEquals(4, articles.size());
    }

    @Test
    public void testGetArticleContent() {
        // Valid URL for testing
        String url = "https://www.prnewswire.co.uk/news-releases/openai-startup-fund--arianna-huffingtons-thrive-global-create-new-company-thrive-ai-health-to-launch-hyper-personalized-ai-health-coach-302190603.html";
        try {
            String content = ArticleExtractor.getArticleContent(url);
            assertNotNull(content);
            assertFalse(content.isEmpty());
        } catch (IOException e) {
            fail("IOException occurred while fetching article content");
        }
    }

    @Test
    public void testPickInterestingNews() {
        // Example news and preferences
        String news = "Title: OpenAI Startup Fund & Arianna Huffington's Thrive Global Create New Company, Thrive AI Health, To Launch Hyper-Personalized AI Health Coach, Link: https://www.prnewswire.co.uk/news-releases/openai-startup-fund--arianna-huffingtons-thrive-global-create-new-company-thrive-ai-health-to-launch-hyper-personalized-ai-health-coach-302190603.html\n" +
                "Title: Hardware Security Modules market is expected to be worth USD 1.27 billion. Hardware Security Modules Market is expected to grow at a 11.8 percentage, Link: https://www.openpr.com/news/3569666hardware-security-modules-market-is-expected-to-be-worth-usd-1-27\n" +
                "Title: 'I Will Take My Business To Costco' - Sam's Club Members Threaten Because Major Perk Ends On Aug.19, Link: https://www.ibtimes.co.uki-will-take-my-business-costco-sams-club-members-threaten-because-major-perk-ends-aug-19-17252992";
        String preferences = "AI, health, security";

        String result = aiService.pickInterestingNews(news, preferences);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains("OpenAI Startup Fund & Arianna Huffington's Thrive Global Create New Company, Thrive AI Health, To Launch Hyper-Personalized AI Health Coach"));
        assertTrue(result.contains("Hardware Security Modules market is expected to be worth USD 1.27 billion"));
        assertFalse(result.contains("Sam's Club Members Threaten Because Major Perk Ends On Aug.19"));
    }
}

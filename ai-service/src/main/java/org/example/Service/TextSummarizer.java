package org.example.Service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.util.List;

@Service
public class TextSummarizer {

    @Value("${hugging.face.api.url}")
    private String huggingFaceApiUrl;

    @Value("${hugging.face.api.key}")
    private String huggingFaceApiKey;

    private String callHuggingFaceApi(String jsonPayload) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(huggingFaceApiUrl);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Authorization", "Bearer " + huggingFaceApiKey);

            StringEntity entity = new StringEntity(jsonPayload);
            httpPost.setEntity(entity);

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String responseString = EntityUtils.toString(response.getEntity());

                if (statusCode == 200) {
                    JSONObject jsonResponse = new JSONObject(responseString);
                    return jsonResponse.getJSONArray("summary_text").getString(0);
                } else {
                    return "Failed to call Hugging Face API: " + statusCode + " - " + responseString;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Exception occurred while calling Hugging Face API";
        }
    }

    public String summarizeTextHugging(String content, int limitLines) {
        if (content == null || content.isEmpty()) {
            return "Content is empty";
        }

        // Split content into lines and limit to the specified number of lines
        List<String> lines = List.of(content.split("\n", limitLines + 1));
        content = String.join("\n", lines.subList(0, Math.min(lines.size(), limitLines)));

        // Construct the JSON payload for the API request
        JSONObject jsonPayload = new JSONObject();
        jsonPayload.put("inputs", new JSONObject().put("article", content));

        // Call the Hugging Face API to get the summary
        String summary = callHuggingFaceApi(jsonPayload.toString());
        return summary != null ? summary.trim() : "Failed to generate summary";
    }

    public static String summarizeText(String content) {
        if (content == null || content.isEmpty()) {
            return null;
        }

        // Split the content into sentences
        String[] sentences = content.split("\\.\\s+");

        StringBuilder summary = new StringBuilder();
        for (int i = 0; i < Math.min(3, sentences.length); i++) {
            summary.append(sentences[i]).append(". ");
        }

        return summary.toString().trim();
    }
}

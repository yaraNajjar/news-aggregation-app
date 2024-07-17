package org.example.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailListener {

    private static final Logger logger = LoggerFactory.getLogger(EmailListener.class);

    @Autowired
    private EmailService emailService;

    /**
     * Listens for messages on the specified Kafka topic, extracts the user ID and news summary,
     * and sends an email with the summary to the user.
     *
     * @param message The message received from Kafka, expected to be in the format "userId:summary".
     */
    @KafkaListener(topics = "${kafka.topic.newsSummaries}", groupId = "${kafka.group.emailServiceGroup}")
    public void listenNewsSummaries(String message) {
        logger.info("Received message from Kafka: {}", message);
        try {
            // Extract userId and summary from message
            String[] parts = message.split(":", 2);
            if (parts.length < 2) {
                logger.error("Invalid message format: {}", message);
                return;
            }
            String userId = parts[0];
            String summary = parts[1];

            // Send email
            emailService.sendEmail(userId, summary);
        } catch (Exception e) {
            logger.error("Error processing message: {}", message, e);
        }
    }
}

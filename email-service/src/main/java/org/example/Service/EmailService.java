package org.example.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${user.service.url}")
    private String userServiceUrl;

    /**
     * Sends an email with the provided summary to the user identified by userId.
     *
     * @param userId  The ID of the user to send the email to.
     * @param summary The summary to include in the email.
     */
    public void sendEmail(String userId, String summary) {
        logger.info("Preparing to send email to user {}", userId);

        try {
            // Fetch user email
            String emailServiceUrl = String.format("%s/users/email/%s", userServiceUrl, userId);
            String to = restTemplate.getForObject(emailServiceUrl, String.class);

            // Check if email was retrieved successfully
            if (to != null) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(to);
                message.setSubject("News Summary");
                message.setText(summary);
                javaMailSender.send(message);
                logger.info("Email sent to user {} with summary: {}", userId, summary);
            } else {
                logger.error("Failed to retrieve email for user {}", userId);
            }
        } catch (Exception e) {
            logger.error("Error sending email to user {}", userId, e);
        }
    }
}

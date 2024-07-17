package org.example.Service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
public class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private RestTemplate restTemplate;

    public EmailServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSendEmail() {
        String userId = "123";
        String summary = "This is a test summary.";
        String userEmail = "user@example.com";

        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(userEmail);

        emailService.sendEmail(userId, summary);

        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    public void testSendEmailFailToRetrieveEmail() {
        String userId = "123";
        String summary = "This is a test summary.";

        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(null);

        emailService.sendEmail(userId, summary);

        verify(javaMailSender, never()).send(any(SimpleMailMessage.class));
    }
}

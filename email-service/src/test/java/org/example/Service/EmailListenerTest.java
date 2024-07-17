package org.example.Service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.*;

@SpringBootTest
public class EmailListenerTest {

    @InjectMocks
    private EmailListener emailListener;

    @Mock
    private EmailService emailService;

    public EmailListenerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testListenNewsSummaries() {
        String message = "123:This is a test summary.";

        emailListener.listenNewsSummaries(message);

        verify(emailService, times(1)).sendEmail("123", "This is a test summary.");
    }

    @Test
    public void testListenNewsSummariesInvalidMessage() {
        String message = "InvalidMessageFormat";

        emailListener.listenNewsSummaries(message);

        verify(emailService, never()).sendEmail(anyString(), anyString());
    }
}

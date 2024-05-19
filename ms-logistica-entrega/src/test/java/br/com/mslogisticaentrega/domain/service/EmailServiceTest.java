package br.com.mslogisticaentrega.domain.service;

import br.com.mslogisticaentrega.domain.valueObject.Email;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

public class EmailServiceTest {
    @Mock
    private JavaMailSender mailSender;
    private EmailService emailService;
    AutoCloseable autoCloseable;

    @BeforeEach
    void setup() {
        autoCloseable = MockitoAnnotations.openMocks(this);

        emailService = new EmailService(mailSender);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void deveEnviarEmail(){
        Email email = new Email("", "", "");

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendEmail(email);

        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}

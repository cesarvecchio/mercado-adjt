package br.com.mslogisticaentrega.domain.service;

import br.com.mslogisticaentrega.domain.valueObject.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(Email email) {
        var mensagem = new SimpleMailMessage();

        mensagem.setFrom("noreply@gmail.com");
        mensagem.setTo(email.destinatario());
        mensagem.setSubject(email.assunto());
        mensagem.setText(email.corpo());

        logger.info("Enviando Relatorio Por Email");

        mailSender.send(mensagem);
    }
}

package com.cdad.project.notificationservice.services;

import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;

@Service
public class EmailNotificationService {

    private final Session session;

    public EmailNotificationService(Session session) {
        this.session = session;
    }

    public void sendEmail(List<String> emails, String subject, String emailMessage){
        try {
            MimeMessage message = new MimeMessage(session);
            String from = "assessment-system@ict.gnu.ac.in";
            message.setFrom(new InternetAddress(from));
            emails.forEach(email->{
                try {
                    message.addRecipients(Message.RecipientType.TO, String.valueOf(new InternetAddress(email)));
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            });
            message.setSubject(subject);
            message.setText(emailMessage);
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}

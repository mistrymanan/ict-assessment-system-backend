package com.cdad.project.notificationservice;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

@SpringBootApplication
public class NotificationServiceApplication {
    private Properties properties;

    public NotificationServiceApplication() {
        this.properties=System.getProperties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");
    }

    @Bean
    public Session getSession(){
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("assessment-system@ict.gnu.ac.in", "Guniict@123$");
            }

        });
        session.setDebug(true);
        return session;
    }
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class,args);
    }
}

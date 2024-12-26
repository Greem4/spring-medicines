package com.greem4.springmedicines.notifier;

import com.greem4.springmedicines.dto.NotificationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailNotifier implements Notifier {

    private final JavaMailSender mailSender;

    @Override
    public void send(NotificationMessage message) {
        var mail = new SimpleMailMessage();
        mail.setTo(message.to());
        mail.setSubject(message.subject());
        mail.setText(message.body());
        mailSender.send(mail);
    }
}

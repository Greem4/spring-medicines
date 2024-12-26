package com.greem4.springmedicines.notifier;

import com.greem4.springmedicines.dto.NotificationMessage;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotifier implements Notifier {

    private final JavaMailSender mailSender;

    @Override
    public void send(NotificationMessage message) {
        try {
            var mimeMessage = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom("medicineSpringMail@yandex.ru", "Срок годности лекарств");

            helper.setTo(message.to());
            helper.setSubject(message.subject());
            helper.setText(message.body(), true);

            mailSender.send(mimeMessage);
            log.info("Письмо успешно отправлено на {}", message.to());

        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Ошибка при отправке письма на {}: {}", message.to(), e.getMessage());
        }
    }
}

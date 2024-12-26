package com.greem4.springmedicines.notifier;

import com.greem4.springmedicines.dto.NotificationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SmsNotifier implements Notifier {

    @Override
    public void send(NotificationMessage message) {
        log.info("Отправка SMS на {}: {}", message.to(), message.body());
    }
}

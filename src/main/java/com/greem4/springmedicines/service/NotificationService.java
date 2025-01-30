package com.greem4.springmedicines.service;

import com.greem4.springmedicines.amqp.NotificationProducer;
import com.greem4.springmedicines.dto.NotificationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationProducer notificationProducer;

    public void sendNotification(NotificationMessage message) {
        notificationProducer.sendNotification(message);
    }
}

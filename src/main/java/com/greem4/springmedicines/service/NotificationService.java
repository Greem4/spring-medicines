package com.greem4.springmedicines.service;

import com.greem4.springmedicines.dto.NotificationMessage;
import com.greem4.springmedicines.notifier.Notifier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final List<Notifier> notifiers;

    public void sendNotification(NotificationMessage message) {
        for (Notifier notifier : notifiers) {
            notifier.send(message);
        }
    }
}

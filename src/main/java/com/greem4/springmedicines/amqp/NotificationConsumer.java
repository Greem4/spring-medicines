package com.greem4.springmedicines.amqp;

import com.greem4.springmedicines.config.RabbitConfig;
import com.greem4.springmedicines.dto.NotificationMessage;
import com.greem4.springmedicines.notifier.EmailNotifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final EmailNotifier emailNotifier;

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void consumeNotification(NotificationMessage message) {
        log.info("Получено сообщение из RabbitMQ: {}", message);
        emailNotifier.sendNotification(message);
    }
}

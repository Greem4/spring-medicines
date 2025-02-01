package com.greem4.springmedicines.listner;

import com.greem4.springmedicines.dto.NotificationMessage;
import com.greem4.springmedicines.notifier.Notifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final List<Notifier> notifiers;

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void consumeNotification(NotificationMessage message) {
        log.info("Получено сообщение из RabbitMQ: {}", message);

        for (Notifier notifier : notifiers) {
            try {
                notifier.sendNotification(message);
            }catch (Exception e) {
                log.error("Ошибка при работе Notifier {}: {}", notifier.getClass().getSimpleName(), e.getMessage());
            }
        }
    }
}

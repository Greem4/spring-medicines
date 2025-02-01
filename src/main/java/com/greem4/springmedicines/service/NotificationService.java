package com.greem4.springmedicines.service;

import com.greem4.springmedicines.dto.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${app.rabbitmq.routingKey}")
    private String routingKey;

    public void sendNotification(NotificationMessage message) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
        log.info("Отправили сообщение {} в RabbitMQ (exchange={}, routingKey={})",
                message, exchangeName, routingKey);
    }
}

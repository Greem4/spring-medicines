package com.greem4.springmedicines.dto;

public record NotificationMessage(
        String to,
        String subject,
        String body
) {
}

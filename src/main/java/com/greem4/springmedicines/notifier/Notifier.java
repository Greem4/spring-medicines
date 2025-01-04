package com.greem4.springmedicines.notifier;

import com.greem4.springmedicines.dto.NotificationMessage;

public interface Notifier {
    // fixme: нелогичное название метода для подобного интерфейса
    void send(NotificationMessage message);
}

package com.greem4.springmedicines.notifier;

import com.greem4.springmedicines.dto.NotificationMessage;

public interface Notifier {

    void send(NotificationMessage message);
}

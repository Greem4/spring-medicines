package com.greem4.springmedicines.integration.scheduler;

import com.greem4.springmedicines.dto.MedicineCreateRequest;
import com.greem4.springmedicines.integration.config.IntegrationTestBase;
import com.greem4.springmedicines.scheduler.ExpiryNotificationScheduler;
import com.greem4.springmedicines.service.MedicineService;
import com.icegreen.greenmail.util.GreenMail;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class ExpiryNotificationSchedulerTest extends IntegrationTestBase {

    @Autowired
    private ExpiryNotificationScheduler scheduler;

    @Autowired
    private MedicineService medicineService;


    @Test
    void notifyMedicinesExpiringSoon_sendEmail() throws Exception {
        var request = new MedicineCreateRequest("Aspirin", "12345", LocalDate.now().plusDays(5));
        medicineService.addMedicine(request);

        scheduler.notifyMedicineExpiringSoon();

        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();

        assertThat(receivedMessages).hasSize(1);

        MimeMessage msg = receivedMessages[0];
        assertThat(msg.getSubject()).isEqualTo("Срок годности лекарства приближается");
        assertThat(msg.getAllRecipients()[0].toString()).isEqualTo("recipient@example.com");
        assertThat(msg.getContent().toString()).contains("Лекарство Aspirin истекает");
    }
}

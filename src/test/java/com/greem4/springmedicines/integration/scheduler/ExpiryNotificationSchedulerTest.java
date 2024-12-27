package com.greem4.springmedicines.integration.scheduler;

import com.greem4.springmedicines.dto.MedicineCreateRequest;
import com.greem4.springmedicines.integration.config.IntegrationTestBase;
import com.greem4.springmedicines.scheduler.ExpiryNotificationScheduler;
import com.greem4.springmedicines.service.MedicineService;
import jakarta.mail.BodyPart;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

public class ExpiryNotificationSchedulerTest extends IntegrationTestBase {

    @Autowired
    private ExpiryNotificationScheduler scheduler;

    @Autowired
    private MedicineService medicineService;


    @Test
    void notifyMedicinesExpiringSoonOneMedicine() throws Exception {
        var request = new MedicineCreateRequest(
                "Aspirin",
                "12345",
                LocalDate.now().plusDays(5));
        medicineService.addMedicine(request);

        scheduler.notifyMedicineExpiringSoon();

        var messages = greenMail.getReceivedMessages();
        assertThat(messages).hasSize(1);

        MimeMessage msg = messages[0];
        assertThat(msg.getSubject())
                .isEqualTo("Уведомление: Срок годности препаратов истекает через неделю");
        assertThat(msg.getAllRecipients()[0].toString()).isEqualTo("test-recipient@mail.com");

        String body = extractBodyContent(msg);

        assertThat(body).contains("Aspirin");
        assertThat(body).contains("12345");
        assertThat(body).contains(LocalDate.now().plusDays(5)
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.forLanguageTag("ru"))));
        assertThat(body).contains("<table");
    }

    @Test
    void notifyMedicinesExpiringSoonTwoMedicines() throws Exception {
        var request1 = new MedicineCreateRequest(
                "Aspirin1",
                "11111",
                LocalDate.now().plusDays(5));
        var request2 = new MedicineCreateRequest(
                "Aspirin2",
                "22222",
                LocalDate.now().plusDays(4));
        medicineService.addMedicine(request1);
        medicineService.addMedicine(request2);

        scheduler.notifyMedicineExpiringSoon();

        var messages = greenMail.getReceivedMessages();
        assertThat(messages).hasSize(1);

        MimeMessage msg = messages[0];
        assertThat(msg.getSubject())
                .isEqualTo("Уведомление: Срок годности препаратов истекает через неделю");
        assertThat(msg.getAllRecipients()[0].toString()).isEqualTo("test-recipient@mail.com");

        String body = extractBodyContent(msg);

        assertThat(body).contains("Aspirin1");
        assertThat(body).contains("11111");
        assertThat(body).contains(LocalDate.now()
                .plusDays(5)
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.forLanguageTag("ru"))));

        assertThat(body).contains("Aspirin2");
        assertThat(body).contains("22222");
        assertThat(body).contains(LocalDate.now()
                .plusDays(4)
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.forLanguageTag("ru"))));
    }

    @Test
    void notifyMedicinesExpiringSoonNoEmailIfExpiryMoreThanWeek(){
        var request = new MedicineCreateRequest("AspirinLong", "99999", LocalDate.now().plusWeeks(2));
        medicineService.addMedicine(request);

        scheduler.notifyMedicineExpiringSoon();

        var messages = greenMail.getReceivedMessages();
        assertThat(messages).isEmpty();
    }

    private String extractContentRecursively(Object content) throws Exception {
        if (content instanceof String text) {
            return text;
        } else if (content instanceof Multipart multipart) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                sb.append(extractContentRecursively(bodyPart.getContent()));
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    private String extractBodyContent(MimeMessage msg) throws Exception {
        Object content = msg.getContent();
        return extractContentRecursively(content);
    }
}

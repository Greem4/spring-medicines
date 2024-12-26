package com.greem4.springmedicines.scheduler;

import com.greem4.springmedicines.database.repository.MedicineRepository;
import com.greem4.springmedicines.dto.MedicineExpiryNotificationDTO;
import com.greem4.springmedicines.dto.NotificationMessage;
import com.greem4.springmedicines.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ExpiryNotificationScheduler {

    private final MedicineRepository medicineRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 9 * * ?")
    public void notifyMedicineExpiringSoon() {
        var oneWeekLater = LocalDate.now().plusWeeks(1);

        var expiringList = medicineRepository.findAllExpiringBefore(oneWeekLater);

        for (MedicineExpiryNotificationDTO data : expiringList) {
            sendExpiryNotification(data);
        }
    }

    private void sendExpiryNotification(MedicineExpiryNotificationDTO data) {
        var subject = "Срок годности лекарства приближается";
        var body = String.format("Срок годности препарата: %s истекает %s.",
                data.name(), data.expiryDate());

        var message = new NotificationMessage(
                "greem4@yandex.ru",
                subject,
                body
        );
        notificationService.sendNotification(message);
    }
}

package com.greem4.springmedicines.scheduler;

import com.greem4.springmedicines.repository.MedicineRepository;
import com.greem4.springmedicines.dto.MedicineExpiryNotificationDTO;
import com.greem4.springmedicines.dto.NotificationMessage;
import com.greem4.springmedicines.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class ExpiryNotificationScheduler {

    @Value("${app.mail.to}")
    private String mailTo;

    private final MedicineRepository medicineRepository;
    private final NotificationService notificationService;
    private final TemplateEngine templateEngine;  // Инжектируем Thymeleaf TemplateEngine

    @Scheduled(cron = "0 0 7 * * ?")
    public void notifyMedicineExpiringSoon() {
        LocalDate limitDate = LocalDate.now().plusWeeks(1);

        int pageNumber = 0;
        int pageSize = 50;
        List<MedicineExpiryNotificationDTO> allDtos = new ArrayList<>();

        Page<MedicineExpiryNotificationDTO> page;
        do {
            PageRequest pageable = PageRequest.of(pageNumber, pageSize);
            page = medicineRepository.findAllExpiringBefore(limitDate, pageable);
            allDtos.addAll(page.getContent());
            pageNumber++;
        } while (page.hasNext());

        if (allDtos.isEmpty()) {
            return;
        }

        String subject = "Уведомление: Срок годности препаратов истекает через неделю";
        String body = buildNotificationBody(allDtos);

        NotificationMessage message = new NotificationMessage(
                mailTo,
                subject,
                body
        );
        notificationService.sendNotification(message);
    }

    private String buildNotificationBody(List<MedicineExpiryNotificationDTO> dtos) {
        var thymeleafContext = new Context(Locale.forLanguageTag("ru"));
        thymeleafContext.setVariable("medicines", dtos);
        return templateEngine.process("expiry-notification", thymeleafContext);
    }
}

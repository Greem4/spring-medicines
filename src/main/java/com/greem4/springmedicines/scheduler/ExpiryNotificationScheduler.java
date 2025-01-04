package com.greem4.springmedicines.scheduler;

import com.greem4.springmedicines.database.repository.MedicineRepository;
import com.greem4.springmedicines.dto.MedicineExpiryNotificationDTO;
import com.greem4.springmedicines.dto.NotificationMessage;
import com.greem4.springmedicines.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class ExpiryNotificationScheduler {

    @Value("${app.mail.to}")
    private String mailTo;

    private final MedicineRepository medicineRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 7 * * ?")
    public void notifyMedicineExpiringSoon() {
        LocalDate limitDate = LocalDate.now().plusWeeks(1);

        // fixme: а если там миллиард айтемов?) мб лучше пагинацию прикрутить?)
        List<MedicineExpiryNotificationDTO> expiringList = medicineRepository.findAllExpiringBefore(limitDate);

        if (expiringList.isEmpty()) {
            return;
        }

        String subject = "Уведомление: Срок годности препаратов истекает через неделю";
        String body = buildNotificationBody(expiringList);

        NotificationMessage message = new NotificationMessage(
                mailTo,
                subject,
                body
        );
        notificationService.sendNotification(message);
    }

    private String buildNotificationBody(List<MedicineExpiryNotificationDTO> dtos) {
        StringBuilder sb = new StringBuilder();
        // fixme: у тебя же таймлиф есть
        sb.append("<html><body>");

        sb.append("<h2 style=\"color:#333333;\">Срок годности истекает через неделю:</h2>");

        sb.append("<table style=\"width:100%; border-collapse: collapse;\">");

        sb.append("<thead>");
        sb.append("<tr>");
        sb.append("<th style=\"border: 1px solid #dddddd; text-align: left; padding: 8px; background-color:#f2f2f2;\">№</th>");
        sb.append("<th style=\"border: 1px solid #dddddd; text-align: left; padding: 8px; background-color:#f2f2f2;\">Название препарата</th>");
        sb.append("<th style=\"border: 1px solid #dddddd; text-align: left; padding: 8px; background-color:#f2f2f2;\">Серия</th>");
        sb.append("<th style=\"border: 1px solid #dddddd; text-align: left; padding: 8px; background-color:#f2f2f2;\">Срок годности</th>");
        sb.append("</tr>");
        sb.append("</thead>");

        sb.append("<tbody>");

        int i = 1;
        for (MedicineExpiryNotificationDTO dto : dtos) {
            String rowColor = (i % 2 == 0) ? "#f9f9f9" : "#ffffff";
            sb.append("<tr style=\"background-color:").append(rowColor).append(";\">");

            sb.append("<td style=\"border: 1px solid #dddddd; padding: 8px;\">").append(i++).append("</td>");

            sb.append("<td style=\"border: 1px solid #dddddd; padding: 8px;\">")
                    .append("<span style=\"color:red; font-weight:bold;\">")
                    .append(escapeHtml(dto.name()))
                    .append("</span>")
                    .append("</td>");

            sb.append("<td style=\"border: 1px solid #dddddd; padding: 8px;\">")
                    .append(escapeHtml(dto.serialNumber()))
                    .append("</td>");

            sb.append("<td style=\"border: 1px solid #dddddd; padding: 8px;\">")
                    .append(escapeHtml(dto.expiryDate()  // TODO однажды подумать над форматом
                            .format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.forLanguageTag("ru")))))
                    .append("</td>");

            sb.append("</tr>");
        }

        sb.append("</tbody>");
        sb.append("</table>");
        sb.append("</body></html>");
        return sb.toString();
    }

    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

}

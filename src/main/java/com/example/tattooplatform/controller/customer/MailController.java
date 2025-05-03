package com.example.tattooplatform.controller.customer;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MailController {

    @Value("${spring.mail.username}")
    private String emailSenderUsername;

    @Value("${mail.receiver.username}")
    private String mailReceiver;

    private final JavaMailSender mailSender;
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final String MAIL_SENT = "mailSent";
    private static final String MAIL_CONFIRMATION_REDIRECT = "redirect:/mail-confirmation";

    public MailController(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @PostMapping("/send-booking-request")
    public String sendEmail(
            @RequestParam String name,
            @RequestParam String phone,
            @RequestParam(required = false) String mail,
            @RequestParam(required = false) String instagram,
            @RequestParam(required = false) String pronoun,
            @RequestParam(required = false) String size,
            @RequestParam(required = false) String placement,
            @RequestParam(required = false) String inspiration,
            @RequestParam(required = false) String misc,
            @RequestParam(value = "attachment", required = false) MultipartFile attachment,
            RedirectAttributes redirectAttributes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailSenderUsername);
            helper.setTo(mailReceiver);
            helper.setSubject("Bokningsförfrågan: " + name);

            String htmlContent = "<p><b>Namn:</b> " + name + "</p>"
                    + (pronoun != null && !pronoun.isEmpty() ? "<p><b>Pronomen:</b> " + pronoun + "</p>" : "")
                    + "<p><b>Telefon:</b> " + phone + "</p>"
                    + (mail != null && !mail.isEmpty() ? "<p><b>Email:</b> " + mail + "</p>" : "")
                    + (instagram != null && !instagram.isEmpty() ? "<p><b>Instagram:</b> @" + instagram + "</p>" : "")
                    + (size != null && !size.isEmpty() ? "<p><b>Storlek:</b> " + size + " cm</p>" : "")
                    + (placement != null && !placement.isEmpty() ? "<p><b>Placering:</b> " + placement + "</p>" : "")
                    + (inspiration != null && !inspiration.isEmpty() ? "<p><b>Inspiration:</b> <a href='" + inspiration + "'>" + inspiration + "</a></p>" : "")
                    + (misc != null && !misc.isEmpty() ? "<p><b>Övrigt:</b> " + misc + "</p>" : "");

            helper.setText(htmlContent, true);

            if (attachment != null && !attachment.isEmpty()) {
                String contentType = attachment.getContentType();
                if (contentType != null && contentType.startsWith("image/")) {
                    if (attachment.getSize() > MAX_FILE_SIZE) {
                        redirectAttributes.addFlashAttribute(MAIL_SENT, false);
                        redirectAttributes.addFlashAttribute("error", "Filen är för stor. Maxstorlek är 5MB.");
                        return MAIL_CONFIRMATION_REDIRECT;
                    }
                    helper.addAttachment(attachment.getOriginalFilename(), attachment);
                } else {
                    redirectAttributes.addFlashAttribute(MAIL_SENT, false);
                    redirectAttributes.addFlashAttribute("error", "Endast bildfiler är tillåtna.");
                    return MAIL_CONFIRMATION_REDIRECT;
                }
            }

            mailSender.send(message);
            redirectAttributes.addFlashAttribute(MAIL_SENT, true);
        } catch (MessagingException e) {
            redirectAttributes.addFlashAttribute(MAIL_SENT, false);
        }

        return MAIL_CONFIRMATION_REDIRECT;
    }


}

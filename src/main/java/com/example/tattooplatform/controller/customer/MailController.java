package com.example.tattooplatform.controller.customer;

import com.example.tattooplatform.controller.ModelFeedback;
import com.example.tattooplatform.services.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

@Controller
public class MailController {
    private final EmailService emailService;
    private final JavaMailSender mailSender;
    private static final String MAIL_CONFIRMATION_REDIRECT = "redirect:/mail-confirmation";

    public MailController(EmailService emailService, JavaMailSender mailSender) {
        this.emailService = emailService;
        this.mailSender = mailSender;
    }

    @PostMapping("/send-booking-request")
    public String sendEmail(
            @RequestParam String name,
            @RequestParam String phone,
            @RequestParam(required = false) String email,
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
            MimeMessageHelper helper = emailService.initializeMessageHelper(message, name);

            if(helper != null){
                helper.setText(emailService.createBookingRequestEmailBody(name, pronoun, phone, email, instagram,
                        size, placement, inspiration, misc), true);
                redirectAttributes = emailService.validateAttachment(redirectAttributes, attachment);

                if (!redirectAttributes.containsAttribute(ModelFeedback.ERROR_MESSAGE.getAttributeKey())) {
                    helper.addAttachment(Objects.requireNonNull(attachment.getOriginalFilename()), attachment);
                    mailSender.send(message);
                    redirectAttributes.addFlashAttribute(ModelFeedback.SUCCESS_MESSAGE.getAttributeKey(),
                            "Tack för din förfrågan. Jag återkommer så snart jag kan.");
                }
            }
        } catch (MessagingException e) {
            redirectAttributes.addFlashAttribute(ModelFeedback.ERROR_MESSAGE.getAttributeKey(),
                    "Det gick inte att skicka din förfrågan. Var vänlig försök igen senare.");
        }

        return MAIL_CONFIRMATION_REDIRECT;
    }

}

package com.example.tattooPlatform.controller.customer;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MailController {

    @Value("${spring.mail.username}")
    private String emailSenderUsername;

    @Value("${mail.receiver.username}")
    private String mailReceiver;

    private final JavaMailSender mailSender;

    public MailController(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @PostMapping("/send-booking-request")
    public String sendEmail(
            @RequestParam String name,
            @RequestParam String phone,
            @RequestParam(required = false) String mail,
            @RequestParam(required = false) String instagram,
            @RequestParam(required = false) String size,
            @RequestParam(required = false) String placement,
            @RequestParam(required = false) String inspiration,
            RedirectAttributes redirectAttributes){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailSenderUsername);
            helper.setTo(mailReceiver);
            helper.setSubject("Bokningsförfrågan: " + name);

            String htmlContent = "<p><b>Namn:</b> " + name + "</p>"
                    + "<p><b>Telefon:</b> " + phone + "</p>"
                    + (mail != null && !mail.isEmpty() ? "<p><b>Email:</b> " + mail + "</p>" : "")
                    + (instagram != null && !instagram.isEmpty() ? "<p><b>Instagram:</b> @" + instagram + "</p>" : "")
                    + (size != null && !size.isEmpty() ? "<p><b>Storlek:</b> " + size + " cm</p>" : "")
                    + (placement != null && !placement.isEmpty() ? "<p><b>Placering:</b> " + placement + "</p>" : "")
                    + (inspiration != null && !inspiration.isEmpty() ? "<p><b>Inspiration:</b> <a href='" + inspiration + "'>" + inspiration + "</a></p>" : "");

            helper.setText(htmlContent, true);

            mailSender.send(message);

            redirectAttributes.addFlashAttribute("mailSent", true);
        } catch (MessagingException e) {
            redirectAttributes.addFlashAttribute("mailSent", false);
        }

        return "redirect:/mail-confirmation";
    }

}

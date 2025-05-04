package com.example.tattooplatform.services;

import com.example.tattooplatform.controller.ModelFeedback;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String emailSenderUsername;

    @Value("${mail.receiver.username}")
    private String mailReceiver;

    public MimeMessageHelper initializeMessageHelper(MimeMessage message, String name){
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(emailSenderUsername);
            helper.setTo(mailReceiver);
            helper.setSubject("Bokningsförfrågan: " + name);
            return helper;
        } catch (MessagingException e) {
            return null;
        }
    }

    public String createBookingRequestEmailBody(String name, String pronoun, String phone, String mail, String instagram,
                                                String size, String placement, String inspiration, String misc){
        return "<p><b>Namn:</b> " + name + "</p>"
                + (pronoun != null && !pronoun.isEmpty() ? "<p><b>Pronomen:</b> " + pronoun + "</p>" : "")
                + "<p><b>Telefon:</b> " + phone + "</p>"
                + (mail != null && !mail.isEmpty() ? "<p><b>Email:</b> " + mail + "</p>" : "")
                + (instagram != null && !instagram.isEmpty() ? "<p><b>Instagram:</b> @" + instagram + "</p>" : "")
                + (size != null && !size.isEmpty() ? "<p><b>Storlek:</b> " + size + " cm</p>" : "")
                + (placement != null && !placement.isEmpty() ? "<p><b>Placering:</b> " + placement + "</p>" : "")
                + (inspiration != null && !inspiration.isEmpty() ? "<p><b>Inspiration:</b> <a href='" + inspiration + "'>" + inspiration + "</a></p>" : "")
                + (misc != null && !misc.isEmpty() ? "<p><b>Övrigt:</b> " + misc + "</p>" : "");
    }

    public RedirectAttributes validateAttachment(RedirectAttributes redirectAttributes, MultipartFile attachment){
        if (attachment != null && !attachment.isEmpty()) {
            String contentType = attachment.getContentType();
            if (contentType != null && contentType.startsWith("image/")) {
                if (attachment.getSize() > 5 * 1024 * 1024L) {
                    redirectAttributes.addFlashAttribute(ModelFeedback.ERROR_MESSAGE.getAttributeKey(),
                            "Filen är för stor. Maxstorlek är 5MB.");
                }
            } else {
                redirectAttributes.addFlashAttribute(ModelFeedback.ERROR_MESSAGE.getAttributeKey(),
                        "Endast bildfiler är tillåtna.");
            }
        }
        return redirectAttributes;
    }
}

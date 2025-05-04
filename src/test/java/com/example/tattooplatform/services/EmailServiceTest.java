package com.example.tattooplatform.services;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
class EmailServiceTest {

    @Autowired EmailService emailService;

    @Value("${spring.mail.username}")
    private String emailSenderUsername;

    @Value("${mail.receiver.username}")
    private String mailReceiver;

    @Test
    void initializeMessageHelper_shouldSetFieldsCorrectly() throws Exception {
        MimeMessage message = new MimeMessage((Session) null);

        MimeMessageHelper helper = emailService.initializeMessageHelper(message, "test user");

        assertNotNull(helper);
        assertEquals(emailSenderUsername, helper.getMimeMessage().getFrom()[0].toString());
        assertEquals(mailReceiver, helper.getMimeMessage().getAllRecipients()[0].toString());
        assertTrue(helper.getMimeMessage().getSubject().contains("test user"));
    }

    @Test
    void createBookingRequestEmailBody_shouldCreateCorrectMessage() {
        String expected = "<p><b>Namn:</b> test name</p>"
                + "<p><b>Pronomen:</b> test pronoun</p>"
                + "<p><b>Telefon:</b> test phone</p>"
                + "<p><b>Email:</b> test mail</p>"
                + "<p><b>Instagram:</b> @test instagram</p>"
                + "<p><b>Storlek:</b> test size cm</p>"
                + "<p><b>Placering:</b> test placement</p>"
                + "<p><b>Inspiration:</b> <a href='test inspiration'>test inspiration</a></p>"
                + "<p><b>Övrigt:</b> test misc</p>";

        assertEquals(expected, emailService.createBookingRequestEmailBody("test name", "test pronoun",
                "test phone", "test mail", "test instagram", "test size",
                "test placement", "test inspiration", "test misc"));
    }

    @Test
    void createBookingRequestEmailBody_shouldIgnoreNullValues(){
        String expected = "<p><b>Namn:</b> test name</p>"
                + "<p><b>Telefon:</b> test phone</p>";

        assertEquals(expected, emailService.createBookingRequestEmailBody("test name", null,
                "test phone", null, null, null, null, null, null));
    }

    @Test
    void validateAttachment_shouldAllowValidImage() {
        MultipartFile mockFile = mock(MultipartFile.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getContentType()).thenReturn("image/png");
        when(mockFile.getSize()).thenReturn(4 * 1024 * 1024L);

        RedirectAttributes result = emailService.validateAttachment(redirectAttributes, mockFile);

        verifyNoInteractions(redirectAttributes);
        assertEquals(redirectAttributes, result);
    }

    @Test
    void validateAttachment_shouldRejectNonImageFile() {
        MultipartFile mockFile = mock(MultipartFile.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getContentType()).thenReturn("application/pdf");

        emailService.validateAttachment(redirectAttributes, mockFile);

        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), contains("Endast bildfiler"));
    }

    @Test
    void validateAttachment_shouldRejectTooLargeFile() {
        MultipartFile mockFile = mock(MultipartFile.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getContentType()).thenReturn("image/jpeg");
        when(mockFile.getSize()).thenReturn(6 * 1024 * 1024L);

        emailService.validateAttachment(redirectAttributes, mockFile);

        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), contains("Filen är för stor"));
    }
}
package com.example.tattooPlatform;

import com.example.tattooPlatform.controller.admin.AdminController;
import com.example.tattooPlatform.controller.admin.BookableDatesController;
import com.example.tattooPlatform.controller.admin.BookingController;
import com.example.tattooPlatform.controller.admin.CustomerController;
import com.example.tattooPlatform.controller.admin.CustomizeWebPageController;
import com.example.tattooPlatform.controller.admin.ImageController;
import com.example.tattooPlatform.controller.admin.ProductController;
import com.example.tattooPlatform.controller.authorization.AuthController;
import com.example.tattooPlatform.controller.customer.CalendarController;
import com.example.tattooPlatform.controller.customer.CustomerPageController;
import com.example.tattooPlatform.controller.customer.MailController;
import com.example.tattooPlatform.controller.error.MyErrorController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
class TattooPlatformTests {
    @Autowired
    private AdminController adminController;
    @Autowired
    private BookableDatesController bookableDatesController;
    @Autowired
    private BookingController bookingController;
    @Autowired
    private CustomerController customerController;
    @Autowired
    private CustomizeWebPageController customizeWebPageController;
    @Autowired
    private ImageController imageController;
    @Autowired
    private ProductController productController;
    @Autowired
    private AuthController authController;
    @Autowired
    private CalendarController calendarController;
    @Autowired
    private CustomerPageController customerPageController;
    @Autowired
    private MyErrorController myErrorController;
    @Autowired
    private MailController mailController;

    @Test
    void contextLoads() {
        assertNotNull(adminController);
        assertNotNull(bookableDatesController);
        assertNotNull(bookingController);
        assertNotNull(customerController);
        assertNotNull(customizeWebPageController);
        assertNotNull(imageController);
        assertNotNull(productController);
        assertNotNull(authController);
        assertNotNull(calendarController);
        assertNotNull(customerPageController);
        assertNotNull(myErrorController);
        assertNotNull(mailController);
    }

}

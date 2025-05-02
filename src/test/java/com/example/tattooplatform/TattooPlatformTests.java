package com.example.tattooplatform;

import com.example.tattooplatform.controller.admin.AdminController;
import com.example.tattooplatform.controller.admin.BookableDateController;
import com.example.tattooplatform.controller.admin.BookingController;
import com.example.tattooplatform.controller.admin.CustomerController;
import com.example.tattooplatform.controller.admin.CustomizeWebPageController;
import com.example.tattooplatform.controller.admin.ImageController;
import com.example.tattooplatform.controller.admin.ProductController;
import com.example.tattooplatform.controller.authorization.AuthController;
import com.example.tattooplatform.controller.customer.CalendarController;
import com.example.tattooplatform.controller.customer.CustomerPageController;
import com.example.tattooplatform.controller.customer.MailController;
import com.example.tattooplatform.controller.error.MyErrorController;
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
    private BookableDateController bookableDateController;
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
        assertNotNull(bookableDateController);
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

package com.example.demo;

import com.example.demo.controller.admin.AdminController;
import com.example.demo.controller.admin.BookableDatesController;
import com.example.demo.controller.admin.BookingController;
import com.example.demo.controller.admin.CustomerController;
import com.example.demo.controller.admin.CustomizeWebPageController;
import com.example.demo.controller.admin.ImageController;
import com.example.demo.controller.admin.ProductController;
import com.example.demo.controller.authorization.AuthController;
import com.example.demo.controller.customer.CalendarController;
import com.example.demo.controller.customer.CustomerPageController;
import com.example.demo.controller.error.MyErrorController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
class Demo3ApplicationTests {
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
    }

}

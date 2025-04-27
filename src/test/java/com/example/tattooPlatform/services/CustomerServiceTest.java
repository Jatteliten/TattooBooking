package com.example.tattooPlatform.services;

import com.example.tattooPlatform.model.Booking;
import com.example.tattooPlatform.model.Customer;
import com.example.tattooPlatform.repos.CustomerRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
class CustomerServiceTest {
    @Autowired
    private CustomerRepo customerRepo;
    @Autowired
    private CustomerService customerService;
    @MockBean
    private BookingService bookingService;

    @AfterEach
    void deleteAll(){
        customerRepo.deleteAll();
    }

    @Test
    void saveCustomer_shouldSaveCustomer() {
        customerService.saveCustomer(new Customer());

        assertEquals(1, customerRepo.findAll().size());
    }

    @Test
    void deleteCustomerAndChangeAssociatedBookingsById_shouldDeleteCustomer() {
        Customer customer = new Customer();
        customerRepo.save(customer);

        assertEquals(1, customerRepo.findAll().size());

        customerService.deleteCustomerAndChangeAssociatedBookings(customer);

        assertEquals(0, customerRepo.findAll().size());
    }

    @Test
    void deleteCustomerAndChangeAssociatedBookings_shouldCallBookingService_ifCustomerHasBookings() {
        Customer customer = Customer.builder()
                .name("test")
                .bookings(List.of(new Booking()))
                .build();

        customerService.deleteCustomerAndChangeAssociatedBookings(customer);

        verify(bookingService).deleteFutureBookingsAndSetPastBookingsToNull(customer.getBookings());
    }

    @Test
    void deleteCustomerAndChangeAssociatedBookings_shouldNotCallBookingServiceIfNoBookings() {
        Customer customer = new Customer();

        customerService.deleteCustomerAndChangeAssociatedBookings(customer);

        verify(bookingService, org.mockito.Mockito.never())
                .deleteFutureBookingsAndSetPastBookingsToNull(org.mockito.Mockito.any());
    }

    @Test
    void getCustomerById_shouldGetCorrectCustomer() {
        Customer customer = Customer.builder()
                .name("test")
                .build();
        customerRepo.save(customer);

        assertEquals("test", customerService.getCustomerById(customer.getId()).getName());
    }

    @Test
    void getCustomerByNameContaining_shouldFindCustomerIfNameContainsText() {
        customerRepo.save(Customer.builder()
                .name("test")
                .build());

        assertFalse(customerService.getCustomerByNameContaining("t").isEmpty());
        assertFalse(customerService.getCustomerByNameContaining("e").isEmpty());
        assertFalse(customerService.getCustomerByNameContaining("s").isEmpty());
        assertFalse(customerService.getCustomerByNameContaining("test").isEmpty());
    }

    @Test
    void getCustomerByNameContaining_shouldNotFindCustomerIfNameDoesNotContainsText() {
        customerRepo.save(Customer.builder()
                .name("test")
                .build());

        assertTrue(customerService.getCustomerByNameContaining("a").isEmpty());
        assertTrue(customerService.getCustomerByNameContaining("b").isEmpty());
        assertTrue(customerService.getCustomerByNameContaining("c").isEmpty());
    }

    @Test
    void getCustomerByNameContaining_shouldFindCorrectCustomer_ifNameContainsText() {
        Customer customerOne = Customer.builder()
                .name("abc")
                .build();
        Customer customerTwo = Customer.builder()
                .name("def")
                .build();
        customerRepo.saveAll(List.of(customerOne, customerTwo));

        List<Customer> foundCustomers = customerService.getCustomerByNameContaining("a");

        assertEquals(1, foundCustomers.size());
        assertEquals(foundCustomers.getFirst().getName(), customerOne.getName());
        assertNotEquals(foundCustomers.getLast().getName(), customerTwo.getName());
    }

    @Test
    void getCustomerIfAtLeastOneContactMethodMatches_shouldFindCorrectCustomer_byInstagram() {
        Customer customer = Customer.builder()
                .name("test")
                .instagram("test")
                .build();
        customerRepo.save(customer);

        Customer foundCustomer = customerService.getCustomerIfAtLeastOneContactMethodMatches(
                Customer.builder()
                        .instagram("test")
                        .build());

        assertEquals(customer.getName(), foundCustomer.getName());
    }

    @Test
    void getCustomerIfAtLeastOneContactMethodMatches_shouldFindCorrectCustomer_byEmail() {
        Customer customer = Customer.builder()
                .name("test")
                .email("test@test.com")
                .build();
        customerRepo.save(customer);

        Customer foundCustomer = customerService.getCustomerIfAtLeastOneContactMethodMatches(
                Customer.builder()
                        .email("test@test.com")
                        .build());

        assertEquals(customer.getName(), foundCustomer.getName());
    }

    @Test
    void getCustomerIfAtLeastOneContactMethodMatches_shouldFindCorrectCustomer_byPhone() {
        Customer customer = Customer.builder()
                .name("test")
                .phone("123456")
                .build();
        customerRepo.save(customer);

        Customer foundCustomer = customerService.getCustomerIfAtLeastOneContactMethodMatches(
                Customer.builder()
                        .phone("123456")
                        .build());

        assertEquals(customer.getName(), foundCustomer.getName());
    }

    @Test
    void getCustomerIfAtLeastOneContactMethodMatches_shouldNotFindIncorrectCustomer_byInstagram() {
        Customer customer = Customer.builder()
                .name("test")
                .instagram("test")
                .build();
        customerRepo.save(customer);

        Customer foundCustomer = customerService.getCustomerIfAtLeastOneContactMethodMatches(
                Customer.builder()
                        .instagram("tester")
                        .build());

        assertNull(foundCustomer);
    }

    @Test
    void getCustomerIfAtLeastOneContactMethodMatches_shouldNotFindIncorrectCustomer_byEmail() {
        Customer customer = Customer.builder()
                .name("test")
                .email("test@test.com")
                .build();
        customerRepo.save(customer);

        Customer foundCustomer = customerService.getCustomerIfAtLeastOneContactMethodMatches(
                Customer.builder()
                        .email("tester@test.com")
                        .build());

        assertNull(foundCustomer);
    }

    @Test
    void getCustomerIfAtLeastOneContactMethodMatches_shouldNotFindIncorrectCustomer_byPhone() {
        Customer customer = Customer.builder()
                .name("test")
                .phone("123456")
                .build();
        customerRepo.save(customer);

        Customer foundCustomer = customerService.getCustomerIfAtLeastOneContactMethodMatches(
                Customer.builder()
                        .phone("654321")
                        .build());

        assertNull(foundCustomer);
    }

    @Test
    void getCustomerIfAtLeastOneContactMethodMatches_shouldNotFindIncorrectCustomer_ifWrongContactMethodMatches() {
        Customer customer = Customer.builder()
                .name("test")
                .phone("test")
                .build();
        customerRepo.save(customer);

        Customer foundCustomer = customerService.getCustomerIfAtLeastOneContactMethodMatches(
                Customer.builder()
                        .instagram("test")
                        .build());

        assertNull(foundCustomer);
    }

    @Test
    void getCustomerByAnyField_shouldFindCorrectCustomer_byInstagram() {
        Customer customer = Customer.builder()
                .name("test")
                .instagram("test")
                .build();
        customerRepo.save(customer);

        assertEquals(customer.getName(), customerService.getCustomerByAnyField(customer.getInstagram()).getName());
    }

    @Test
    void getCustomerByAnyField_shouldFindCorrectCustomer_byPhone() {
        Customer customer = Customer.builder()
                .name("test")
                .phone("test")
                .build();
        customerRepo.save(customer);

        assertEquals(customer.getName(), customerService.getCustomerByAnyField(customer.getPhone()).getName());
    }

    @Test
    void getCustomerByAnyField_shouldFindCorrectCustomer_byEmail() {
        Customer customer = Customer.builder()
                .name("test")
                .email("test")
                .build();
        customerRepo.save(customer);

        assertEquals(customer.getName(), customerService.getCustomerByAnyField(customer.getEmail()).getName());
    }

    @Test
    void getCustomerByAnyField_shouldNotFindCustomer_ifNoFieldMatches() {
        Customer customer = Customer.builder()
                .name("test")
                .email("test")
                .phone("test")
                .instagram("test")
                .build();
        customerRepo.save(customer);

        assertNull(customerService.getCustomerByAnyField("tester"));
    }

    @Test
    void getCustomersEligiblePreviousBookingsForTouchUp_shouldAddPreviousBooking_forTouchUp() {
        Booking previousBooking = Booking.builder()
                .date(LocalDateTime.now().minusDays(1))
                .build();
        Customer customer = Customer.builder()
                .bookings(List.of(previousBooking))
                .build();

        customerRepo.save(customer);

        List<Booking> bookingsForTouchUp = customerService.getCustomersEligiblePreviousBookingsForTouchUp(customer,
                Booking.builder()
                        .date(LocalDateTime.now())
                        .build());

        assertTrue(bookingsForTouchUp.contains(previousBooking));
    }

    @Test
    void getCustomersEligiblePreviousBookingsForTouchUp_shouldNotAddPreviousBooking_ifBookingIsAlreadyTouchedUp() {
        Booking previousBooking = Booking.builder()
                .date(LocalDateTime.now().minusDays(1))
                .touchUp(true)
                .previousBooking(Booking.builder()
                        .date(LocalDateTime.now().minusDays(2))
                        .build())
                .build();
        Customer customer = Customer.builder()
                .bookings(List.of(previousBooking))
                .build();

        customerRepo.save(customer);

        List<Booking> bookingsForTouchUp = customerService.getCustomersEligiblePreviousBookingsForTouchUp(customer,
                Booking.builder()
                        .date(LocalDateTime.now())
                        .build());

        assertFalse(bookingsForTouchUp.contains(previousBooking));
    }

    @Test
    void getCustomersEligiblePreviousBookingsForTouchUp_shouldAddCorrectBooking() {
        Booking previousTouchedUpBooking = Booking.builder()
                .date(LocalDateTime.now().minusDays(2))
                .touchUp(true)
                .previousBooking(Booking.builder()
                        .date(LocalDateTime.now().minusDays(3))
                        .build())
                .build();
        Booking previousNonTouchedUpBooking = Booking.builder()
                .date(LocalDateTime.now().minusDays(1))
                .build();
        Customer customer = Customer.builder()
                .bookings(List.of(previousTouchedUpBooking, previousNonTouchedUpBooking))
                .build();

        customerRepo.save(customer);

        List<Booking> bookingsForTouchUp = customerService.getCustomersEligiblePreviousBookingsForTouchUp(customer,
                Booking.builder()
                        .date(LocalDateTime.now())
                        .build());

        assertFalse(bookingsForTouchUp.contains(previousTouchedUpBooking));
        assertTrue(bookingsForTouchUp.contains(previousNonTouchedUpBooking));
    }

    @Test
    void calculateCustomersTotalPaid_shouldReturnCorrectAmount(){
        int price = 1000;
        Customer customer = Customer.builder()
                .bookings(
                        List.of(Booking.builder()
                                .finalPrice(price/2)
                                .build(),
                        Booking.builder()
                                .finalPrice(price/2)
                                .build()))
                .build();

        assertEquals(price, customerService.calculateCustomersTotalPaid(customer));
    }

    @Test
    void calculateCustomersTotalPaid_shouldNotInclude_bookingsWithoutFinalPrice(){
        int price = 1000;
        Customer customer = Customer.builder()
                .bookings(
                        List.of(Booking.builder()
                                        .finalPrice(price/2)
                                        .build(),
                                Booking.builder()
                                        .build()))
                .build();

        assertEquals(price/2, customerService.calculateCustomersTotalPaid(customer));
    }
}
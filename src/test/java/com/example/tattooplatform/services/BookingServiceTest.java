package com.example.tattooplatform.services;

import com.example.tattooplatform.model.BookableDate;
import com.example.tattooplatform.model.BookableHour;
import com.example.tattooplatform.model.Booking;
import com.example.tattooplatform.model.Customer;
import com.example.tattooplatform.model.TattooImage;
import com.example.tattooplatform.repos.BookingRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
class BookingServiceTest {
    @Autowired
    private BookingRepo bookingRepo;
    @Autowired
    private BookingService bookingService;
    @MockBean
    private BookableDateService bookableDateService;
    @MockBean
    private TattooImageService tattooImageService;
    @MockBean
    private S3ImageService s3ImageService;
    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalTime TEN_O_CLOCK = LocalTime.of(10, 0);
    private static final LocalTime ELEVEN_O_CLOCK = LocalTime.of(11, 0);
    private static final LocalTime TWELVE_O_CLOCK = LocalTime.of(12, 0);

    @AfterEach
    void deleteAllBookings(){
        bookingRepo.deleteAll();
    }

    @Test
    void saveBooking_shouldSaveBooking(){
        bookingService.saveBooking(new Booking());

        assertEquals(1, bookingRepo.findAll().size());
    }

    @Test
    void saveBookings_shouldSaveAllBookings_inList(){
        bookingService.saveListOfBookings(List.of(new Booking(), new Booking()));

        assertEquals(2, bookingRepo.findAll().size());
    }

    @Test
    void getBookingById_shouldGetCorrectBooking(){
        String notes = "test";
        Booking booking = Booking.builder()
                .date(TODAY.atTime(TEN_O_CLOCK))
                .endTime(TODAY.atTime(ELEVEN_O_CLOCK))
                .depositPaid(true)
                .finalPrice(500)
                .notes(notes)
                .build();
        bookingRepo.save(booking);

        Booking bookingById = bookingService.getBookingById(booking.getId());

        assertEquals(bookingById.getDate(), TODAY.atTime(TEN_O_CLOCK));
        assertEquals(bookingById.getEndTime(), TODAY.atTime(ELEVEN_O_CLOCK));
        assertTrue(bookingById.isDepositPaid());
        assertEquals(500, bookingById.getFinalPrice());
        assertEquals(bookingById.getNotes(), notes);
    }

    @Test
    void getBookingById_shouldReturnNull_ifBookingDoesNotExist(){
        assertNull(bookingService.getBookingById(UUID.randomUUID()));
    }

    @Test
    void deleteBooking_shouldDeleteBooking(){
        Booking booking = Booking.builder()
                .date(TODAY.atTime(TEN_O_CLOCK))
                .build();
        bookingRepo.save(booking);

        assertEquals(1, bookingRepo.findAll().size());

        bookingService.deleteBooking(booking);

        assertEquals(0, bookingRepo.findAll().size());
    }

    @Test
    void deleteBooking_shouldSetBookedBookableHoursWithinBookingHoursToAvailable(){
        Booking booking = Booking.builder()
                .date(TODAY.atTime(TEN_O_CLOCK))
                .endTime(TODAY.atTime(TWELVE_O_CLOCK))
                .build();
        BookableDate bookableDate = BookableDate.builder()
                .fullyBooked(true)
                .date(TODAY)
                .bookableHours(List.of(BookableHour.builder()
                        .hour(ELEVEN_O_CLOCK)
                        .booked(true)
                        .build()))
                .build();

        Mockito.when(bookableDateService.getBookableDateByDate(TODAY)).thenReturn(bookableDate);

        bookingService.deleteBooking(booking);

        assertFalse(bookableDate.isFullyBooked());
        assertFalse(bookableDate.getBookableHours().get(0).isBooked());
    }

    @Test
    void deleteBooking_shouldDeleteAssociatedTattooImageAndS3Image() {
        TattooImage tattooImage = TattooImage.builder()
                .url("test")
                .build();
        Booking booking = Booking.builder()
                .date(TODAY.atTime(TEN_O_CLOCK))
                .tattooImage(tattooImage)
                .build();

        bookingService.deleteBooking(booking);

        Mockito.verify(tattooImageService).deleteTattooImage(tattooImage);
        Mockito.verify(s3ImageService).deleteImage("test");
    }

    @Test
    void deleteBookings_shouldDeleteBookings(){
        List<Booking> bookings = List.of(
                Booking.builder()
                        .date(TODAY.atTime(TEN_O_CLOCK))
                        .build(),
                Booking.builder()
                        .date(TODAY.atTime(TWELVE_O_CLOCK))
                        .build());

        bookingRepo.saveAll(bookings);

        assertEquals(2, bookingRepo.findAll().size());

        bookingService.deleteBookings(bookings);

        assertEquals(0, bookingRepo.findAll().size());
    }

    @Test
    void deleteBookings_shouldSetBookedBookableHoursWithinBookingsHoursToAvailable(){
        Booking bookingOne = Booking.builder()
                .date(TODAY.atTime(TEN_O_CLOCK))
                .endTime(TODAY.atTime(TWELVE_O_CLOCK))
                .build();
        Booking bookingTwo = Booking.builder()
                .date(TODAY.atTime(TEN_O_CLOCK).plusDays(1))
                .endTime(TODAY.atTime(TWELVE_O_CLOCK).plusDays(1))
                .build();
        BookableDate bookableDateOne = BookableDate.builder()
                .fullyBooked(true)
                .date(TODAY)
                .bookableHours(List.of(BookableHour.builder()
                        .hour(ELEVEN_O_CLOCK)
                        .booked(true)
                        .build()))
                .build();
        BookableDate bookableDateTwo = BookableDate.builder()
                .fullyBooked(true)
                .date(TODAY.plusDays(1))
                .bookableHours(List.of(BookableHour.builder()
                        .hour(ELEVEN_O_CLOCK)
                        .booked(true)
                        .build()))
                .build();

        Mockito.when(bookableDateService.getBookableDateByDate(TODAY)).thenReturn(bookableDateOne);
        Mockito.when(bookableDateService.getBookableDateByDate(TODAY.plusDays(1))).thenReturn(bookableDateTwo);

        bookingService.deleteBookings(List.of(bookingOne, bookingTwo));

        assertFalse(bookableDateOne.isFullyBooked());
        assertFalse(bookableDateOne.getBookableHours().get(0).isBooked());
        assertFalse(bookableDateTwo.isFullyBooked());
        assertFalse(bookableDateTwo.getBookableHours().get(0).isBooked());
    }

    @Test
    void deleteBookings_shouldDeleteAssociatedTattooImagesAndS3Images() {
        TattooImage tattooImageOne = TattooImage.builder()
                .url("test1")
                .build();
        TattooImage tattooImageTwo = TattooImage.builder()
                .url("test2")
                .build();
        List<TattooImage> tattooImages = List.of(tattooImageOne, tattooImageTwo);
        Booking bookingOne = Booking.builder()
                .date(TODAY.atTime(TEN_O_CLOCK))
                .tattooImage(tattooImageOne)
                .build();
        Booking bookingTwo = Booking.builder()
                .date(TODAY.atTime(TWELVE_O_CLOCK))
                .tattooImage(tattooImageTwo)
                .build();
        List<Booking> bookings = List.of(bookingOne, bookingTwo);

        bookingService.deleteBookings(bookings);

        Mockito.verify(tattooImageService).deleteListOfTattooImages(tattooImages);
        Mockito.verify(s3ImageService).deleteImages(List.of("test1", "test2"));
    }

    @Test
    void deleteFutureBookingsAndSetPastBookingsToNull_shouldNotDeletePastBookings(){
        Booking booking = Booking.builder()
                .date(TODAY.minusDays(1).atTime(TEN_O_CLOCK))
                .build();

        bookingRepo.save(booking);

        bookingService.deleteFutureBookingsAndSetPastBookingsToNull(List.of(booking));

        assertEquals(1, bookingRepo.findAll().size());
    }

    @Test
    void deleteFutureBookingsAndSetPastBookingsToNull_shouldDeleteFutureBookings(){
        Booking booking = Booking.builder()
                .date(TODAY.plusDays(1).atTime(TEN_O_CLOCK))
                .build();

        bookingRepo.save(booking);

        bookingService.deleteFutureBookingsAndSetPastBookingsToNull(List.of(booking));

        assertEquals(0, bookingRepo.findAll().size());
    }

    @Test
    void deleteFutureBookingsAndSetPastBookingsToNull_shouldSetPastBookingCustomerToNull(){
        Booking booking = Booking.builder()
                .date(TODAY.minusDays(1).atTime(TEN_O_CLOCK))
                .customer(new Customer())
                .build();

        bookingService.deleteFutureBookingsAndSetPastBookingsToNull(List.of(booking));

        assertNull(booking.getCustomer());
    }

    @Test
    void deleteFutureBookingsAndSetPastBookingsToNull_shouldSetFutureApplicableBookableDatesToAvailable(){
        Booking booking = Booking.builder()
                .date(TODAY.plusDays(1).atTime(TEN_O_CLOCK))
                .endTime(TODAY.plusDays(1).atTime(TWELVE_O_CLOCK))
                .build();
        BookableDate bookableDate = BookableDate.builder()
                .date(TODAY.plusDays(1))
                .fullyBooked(true)
                .bookableHours(List.of(BookableHour.builder()
                        .hour(ELEVEN_O_CLOCK)
                        .booked(true)
                        .build()))
                .build();

        bookingRepo.save(booking);

        Mockito.when(bookableDateService.getBookableDateByDate(TODAY.plusDays(1))).thenReturn(bookableDate);

        bookingService.deleteFutureBookingsAndSetPastBookingsToNull(List.of(booking));

        assertFalse(bookableDate.isFullyBooked());
        assertFalse(bookableDate.getBookableHours().get(0).isBooked());
    }

    @Test
    void getBookingsByDate_shouldGetCorrectBookings(){
        Booking bookingOne = Booking.builder()
                .date(TODAY.atTime(TEN_O_CLOCK))
                .build();
        Booking bookingTwo = Booking.builder()
                .date(TODAY.atTime(TWELVE_O_CLOCK))
                .build();

        bookingRepo.saveAll(List.of(bookingOne, bookingTwo));

        List<Booking> bookings = bookingService.getBookingsByDate(TODAY);

        assertTrue(bookings.contains(bookingOne));
        assertTrue(bookings.contains(bookingTwo));
    }

    @Test
    void getBookingsByDate_shouldNotGetIncorrectBookings(){
        Booking bookingOne = Booking.builder()
                .date(TODAY.plusDays(1).atTime(TEN_O_CLOCK))
                .build();
        Booking bookingTwo = Booking.builder()
                .date(TODAY.plusDays(2).atTime(TWELVE_O_CLOCK))
                .build();

        bookingRepo.saveAll(List.of(bookingOne, bookingTwo));

        List<Booking> bookings = bookingService.getBookingsByDate(TODAY);

        assertFalse(bookings.contains(bookingOne));
        assertFalse(bookings.contains(bookingTwo));
    }

    @Test
    void getBookingsBetweenTwoGivenDates_shouldReturnBookingsInsideDateRange(){
        Booking bookingOne = Booking.builder()
                .date(TODAY.atTime(TEN_O_CLOCK))
                .endTime(TODAY.atTime(ELEVEN_O_CLOCK))
                .build();
        Booking bookingTwo = Booking.builder()
                .date(TODAY.plusDays(1).atTime(TEN_O_CLOCK))
                .endTime(TODAY.atTime(ELEVEN_O_CLOCK))
                .build();
        List<Booking> bookings = List.of(bookingOne, bookingTwo);

        bookingRepo.saveAll(bookings);

        List<Booking> foundBookings = bookingService.getBookingsBetweenTwoGivenDates(
                TODAY.minusDays(1).atStartOfDay(), TODAY.plusDays(2).atStartOfDay());

        assertTrue(foundBookings.contains(bookingOne) && foundBookings.contains(bookingTwo));
    }

    @Test
    void getBookingsBetweenTwoGivenDates_shouldNotReturnBookingsOutsideDateRange(){
        Booking bookingOne = Booking.builder()
                .date(TODAY.atTime(TEN_O_CLOCK))
                .endTime(TODAY.atTime(ELEVEN_O_CLOCK))
                .build();
        Booking bookingTwo = Booking.builder()
                .date(TODAY.plusDays(1).atTime(TEN_O_CLOCK))
                .endTime(TODAY.atTime(ELEVEN_O_CLOCK))
                .build();
        List<Booking> bookings = List.of(bookingOne, bookingTwo);

        bookingRepo.saveAll(bookings);

        List<Booking> foundBookings = bookingService.getBookingsBetweenTwoGivenDates(
                TODAY.plusDays(2).atStartOfDay(), TODAY.plusDays(3).atStartOfDay());

        assertTrue(foundBookings.isEmpty());
    }

    @Test
    void getBookingsBetweenTwoGivenDates_shouldOnlyReturnBookingsInsideDateRange(){
        Booking bookingOne = Booking.builder()
                .date(TODAY.atTime(TEN_O_CLOCK))
                .endTime(TODAY.atTime(ELEVEN_O_CLOCK))
                .build();
        Booking bookingTwo = Booking.builder()
                .date(TODAY.plusDays(2).atTime(TEN_O_CLOCK))
                .endTime(TODAY.atTime(ELEVEN_O_CLOCK))
                .build();
        List<Booking> bookings = List.of(bookingOne, bookingTwo);

        bookingRepo.saveAll(bookings);

        List<Booking> foundBookings = bookingService.getBookingsBetweenTwoGivenDates(
                TODAY.minusDays(1).atStartOfDay(), TODAY.plusDays(1).atStartOfDay());

        assertTrue(foundBookings.contains(bookingOne));
        assertFalse(foundBookings.contains(bookingTwo));
    }

    @Test
    void setBookableHoursRelatedToBookingToAvailable_shouldSetBookedHoursInRangeToAvailable_andSetBookableDateToAvailable(){
        Booking booking = Booking.builder()
                .date(TODAY.atTime(TEN_O_CLOCK))
                .endTime(TODAY.atTime(TWELVE_O_CLOCK))
                .build();
        BookableDate bookableDate = BookableDate.builder()
                .date(TODAY)
                .fullyBooked(true)
                .bookableHours(List.of(BookableHour.builder()
                        .hour(ELEVEN_O_CLOCK)
                        .booked(true)
                        .build()))
                .build();

        Mockito.when(bookableDateService.getBookableDateByDate(TODAY)).thenReturn(bookableDate);

        BookableDate updatedBookableDate = bookingService.setBookableHoursRelatedToBookingToAvailable(booking);

        assertFalse(updatedBookableDate.isFullyBooked());
        assertFalse(updatedBookableDate.getBookableHours().get(0).isBooked());
    }

    @Test
    void setBookableHoursRelatedToBookingToAvailable_shouldNotSetBookedHoursOutsideRangeToAvailable(){
        Booking booking = Booking.builder()
                .date(TODAY.atTime(TEN_O_CLOCK))
                .endTime(TODAY.atTime(ELEVEN_O_CLOCK))
                .build();
        BookableDate bookableDate = BookableDate.builder()
                .date(TODAY)
                .fullyBooked(true)
                .bookableHours(List.of(BookableHour.builder()
                        .hour(TWELVE_O_CLOCK)
                        .booked(true)
                        .build()))
                .build();

        Mockito.when(bookableDateService.getBookableDateByDate(TODAY)).thenReturn(bookableDate);

        BookableDate updatedBookableDate = bookingService.setBookableHoursRelatedToBookingToAvailable(booking);

        assertTrue(updatedBookableDate.isFullyBooked());
        assertTrue(updatedBookableDate.getBookableHours().get(0).isBooked());
    }

    @Test
    void setBookableHoursRelatedToBookingToAvailable_shouldOnlySetBookedHoursInsideRangeToAvailable_andSetBookableDateToAvailable(){
        Booking booking = Booking.builder()
                .date(TODAY.atTime(TEN_O_CLOCK))
                .endTime(TODAY.atTime(ELEVEN_O_CLOCK))
                .build();
        BookableHour bookableHourOne = BookableHour.builder()
                .hour(TEN_O_CLOCK.plusMinutes(30))
                .booked(true)
                .build();
        BookableHour bookableHourTwo = BookableHour.builder()
                .hour(TWELVE_O_CLOCK)
                .booked(true)
                .build();
        BookableDate bookableDate = BookableDate.builder()
                .date(TODAY)
                .fullyBooked(true)
                .bookableHours(List.of(bookableHourOne, bookableHourTwo))
                .build();

        Mockito.when(bookableDateService.getBookableDateByDate(TODAY)).thenReturn(bookableDate);

        BookableDate updatedBookableDate = bookingService.setBookableHoursRelatedToBookingToAvailable(booking);

        assertFalse(updatedBookableDate.isFullyBooked());
        assertFalse(bookableHourOne.isBooked());
        assertTrue(bookableHourTwo.isBooked());
    }

    @Test
    void setBookableHoursRelatedToBookingToAvailable_shouldSetBookableHourAtStartTimeToAvailable_andSetBookableDateToAvailable(){
        Booking booking = Booking.builder()
                .date(TODAY.atTime(TEN_O_CLOCK))
                .endTime(TODAY.atTime(TWELVE_O_CLOCK))
                .build();
        BookableDate bookableDate = BookableDate.builder()
                .date(TODAY)
                .fullyBooked(true)
                .bookableHours(List.of(BookableHour.builder()
                        .hour(TEN_O_CLOCK)
                        .booked(true)
                        .build()))
                .build();

        Mockito.when(bookableDateService.getBookableDateByDate(TODAY)).thenReturn(bookableDate);

        BookableDate updatedBookableDate = bookingService.setBookableHoursRelatedToBookingToAvailable(booking);

        assertFalse(updatedBookableDate.isFullyBooked());
        assertFalse(updatedBookableDate.getBookableHours().get(0).isBooked());
    }

    @Test
    void checkIfBookingOverlapsWithAlreadyBookedHours_shouldReturnTrue_ifBookingStartHourExistsInsideTimeRange(){
        Booking booking = Booking.builder()
                .date(TODAY.atTime(ELEVEN_O_CLOCK))
                .endTime(TODAY.atTime(TWELVE_O_CLOCK))
                .build();
        bookingRepo.save(booking);

        assertTrue(bookingService.checkIfBookingOverlapsWithAlreadyBookedHours(
                TODAY.atTime(TEN_O_CLOCK), TODAY.atTime(ELEVEN_O_CLOCK.plusMinutes(1))));
    }

    @Test
    void checkIfBookingOverlapsWithAlreadyBookedHours_shouldReturnTrue_ifBookingEndHourExistsInsideTimeRange(){
        Booking booking = Booking.builder()
                .date(TODAY.atTime(TEN_O_CLOCK))
                .endTime(TODAY.atTime(ELEVEN_O_CLOCK))
                .build();
        bookingRepo.save(booking);

        assertTrue(bookingService.checkIfBookingOverlapsWithAlreadyBookedHours(
                TODAY.atTime(ELEVEN_O_CLOCK.minusMinutes(1)), TODAY.atTime(TWELVE_O_CLOCK)));
    }

    @Test
    void checkIfBookingOverlapsWithAlreadyBookedHours_shouldReturnTrue_ifBookingEndAndStartTimeExistsWithinTimeRange(){
        Booking booking = Booking.builder()
                .date(TODAY.atTime(ELEVEN_O_CLOCK))
                .endTime(TODAY.atTime(ELEVEN_O_CLOCK.plusMinutes(1)))
                .build();
        bookingRepo.save(booking);

        assertTrue(bookingService.checkIfBookingOverlapsWithAlreadyBookedHours(
                TODAY.atTime(TEN_O_CLOCK), TODAY.atTime(TWELVE_O_CLOCK)));
    }

    @Test
    void checkIfBookingOverlapsWithAlreadyBookedHours_shouldReturnFalse_ifBookingDoesNotExistInsideTimeRange(){
        Booking booking = Booking.builder()
                .date(TODAY.atTime(TWELVE_O_CLOCK))
                .endTime(TODAY.atTime(TWELVE_O_CLOCK.plusHours(1)))
                .build();
        bookingRepo.save(booking);

        assertFalse(bookingService.checkIfBookingOverlapsWithAlreadyBookedHours(
                TODAY.atTime(TEN_O_CLOCK), TODAY.atTime(ELEVEN_O_CLOCK)));
    }

    @Test
    void checkIfBookingOverlapsWithAlreadyBookedHours_shouldReturnFalse_ifBookingStartMatchesGivenEndHour(){
        Booking booking = Booking.builder()
                .date(TODAY.atTime(ELEVEN_O_CLOCK))
                .endTime(TODAY.atTime(TWELVE_O_CLOCK))
                .build();
        bookingRepo.save(booking);

        assertFalse(bookingService.checkIfBookingOverlapsWithAlreadyBookedHours(
                TODAY.atTime(TEN_O_CLOCK), TODAY.atTime(ELEVEN_O_CLOCK)));
    }

    @Test
    void checkIfBookingOverlapsWithAlreadyBookedHours_shouldReturnFalse_ifBookingEndMatchesGivenStartHour(){
        Booking booking = Booking.builder()
                .date(TODAY.atTime(TEN_O_CLOCK))
                .endTime(TODAY.atTime(ELEVEN_O_CLOCK))
                .build();
        bookingRepo.save(booking);

        assertFalse(bookingService.checkIfBookingOverlapsWithAlreadyBookedHours(
                TODAY.atTime(ELEVEN_O_CLOCK), TODAY.atTime(TWELVE_O_CLOCK)));
    }

    @Test
    void uploadTattooImage_shouldThrowError_ifBookingIsNull(){
        assertThrows(IllegalArgumentException.class, () ->
                bookingService.uploadTattooImage(null, null, null));
    }

    @Test
    void uploadTattooImage_shouldThrowError_ifCategoryIdsIsNull(){
        assertThrows(IllegalArgumentException.class, () ->
                bookingService.uploadTattooImage(new Booking(), null, null));
    }

    @Test
    void uploadTattooImage_shouldThrowError_ifCategoryIdsIsEmpty(){
        assertThrows(IllegalArgumentException.class, () ->
                bookingService.uploadTattooImage(new Booking(), null, List.of()));
    }

    @Test
    void deleteTattooImageFromBooking_shouldSetBookingTattooImageToNull(){
        Booking booking = Booking.builder()
                .tattooImage(TattooImage.builder()
                        .url("test")
                        .build())
                .build();

        bookingService.deleteTattooImageFromBooking(booking);

        assertNull(booking.getTattooImage());
    }

    @Test
    void deleteTattooImageFromBooking_shouldThrowError_ifBookingHasNoTattooImage(){
        Booking booking = new Booking();


        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                bookingService.deleteTattooImageFromBooking(booking));

        assertEquals("Booking does not have a tattoo image.", exception.getMessage());
    }

    @Test
    void deleteTattooImageFromBooking_shouldCallImageDeleteMethods(){
        TattooImage tattooImage = TattooImage.builder()
                .url("test")
                .build();
        Booking booking = Booking.builder()
                .tattooImage(tattooImage)
                .build();

        bookingService.deleteTattooImageFromBooking(booking);

        Mockito.verify(tattooImageService).deleteTattooImage(tattooImage);
        Mockito.verify(s3ImageService).deleteImage("test");
    }

    @Test
    void sortBookingsByStartDateAndTime_shouldSortCorrectly(){
        List<Booking> bookings = List.of(
                Booking.builder()
                        .date(TODAY.atTime(TWELVE_O_CLOCK))
                        .build(),
                Booking.builder()
                        .date(TODAY.plusDays(1).atTime(TWELVE_O_CLOCK))
                        .build(),
                Booking.builder()
                        .date(TODAY.minusDays(1).atTime(TWELVE_O_CLOCK))
                        .build(),
                Booking.builder()
                        .date(TODAY.atTime(TEN_O_CLOCK))
                        .build());

        List<Booking> sortedList = bookingService.sortBookingsByStartDateAndTime(bookings);

        for(int i = 1; i < sortedList.size(); i++){
            assertTrue(sortedList.get(i).getDate().isAfter(sortedList.get(i-1).getDate()));
        }
    }

    @Test
    void createBookingSuccessMessage_shouldCreateCorrectMessage(){
        Customer customer = Customer.builder()
                .name("Test")
                .build();

        assertEquals("Test booked at " + TEN_O_CLOCK + " - " + ELEVEN_O_CLOCK + " on " + TODAY,
                bookingService.createBookingSuccessMessage(customer, TEN_O_CLOCK, ELEVEN_O_CLOCK, TODAY));
    }

    @Test
    void calculateTotalCostForBookings_shouldCalculateCorrectly() {
        int price = 500;
        Booking bookingOne = Booking.builder()
                .finalPrice(price)
                .build();
        Booking bookingTwo = Booking.builder()
                .finalPrice(price*2)
                .build();

        assertEquals(price*3, bookingService.calculateTotalCostForBookings(List.of(bookingOne, bookingTwo)));
    }

}
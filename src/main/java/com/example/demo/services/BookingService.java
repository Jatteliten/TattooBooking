package com.example.demo.services;

import com.example.demo.model.BookableDate;
import com.example.demo.model.BookableHour;
import com.example.demo.model.Booking;
import com.example.demo.dtos.bookingdtos.BookingCustomerDepositTimeDto;
import com.example.demo.dtos.bookingdtos.BookingWithoutIdDto;
import com.example.demo.model.ImageCategory;
import com.example.demo.model.TattooImage;
import com.example.demo.repos.BookingRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {
    private final BookingRepo bookingRepo;
    private final BookableDateService bookableDateService;
    private final S3ImageService s3ImageService;
    private final TattooImageService tattooImageService;
    private final ImageCategoryService imageCategoryService;

    public BookingService(BookingRepo bookingRepo, BookableDateService bookableDateService, S3ImageService s3ImageService, TattooImageService tattooImageService, ImageCategoryService imageCategoryService){
        this.bookingRepo = bookingRepo;
        this.bookableDateService = bookableDateService;
        this.s3ImageService = s3ImageService;
        this.tattooImageService = tattooImageService;
        this.imageCategoryService = imageCategoryService;
    }

    public List<Booking> getBookingsByDate(LocalDate date){
        LocalDateTime startOfDate = date.atStartOfDay();
        LocalDateTime endOfDate = date.atTime(23, 59, 59);
        return bookingRepo.findByDateBetween(startOfDate, endOfDate);
    }

    public Booking getBookingById(UUID id){
        return bookingRepo.findById(id).orElse(null);
    }

    public void saveBooking(Booking booking){
        bookingRepo.save(booking);
    }

    public void saveListOfBookings(List<Booking> bookings){
        bookingRepo.saveAll(bookings);
    }

    public List<Booking> getAllBookings(){
        return bookingRepo.findAll();
    }

    @Transactional
    public void deleteBooking(Booking booking){
        setBookableHoursRelatedToBookingToAvailable(booking);
        s3ImageService.deleteImage(booking.getTattooImage().getUrl());
        tattooImageService.deleteTattooImage(booking.getTattooImage());
        bookingRepo.delete(booking);
    }

    private void setBookableHoursRelatedToBookingToAvailable(Booking booking){
        BookableDate bookableDate = bookableDateService.getBookableDateByDate(LocalDate.from(booking.getDate()));
        List<BookableHour> bookableHoursOnDate = bookableDate.getBookableHours();
        for(BookableHour bookableHour: bookableHoursOnDate){
            LocalTime hour = bookableHour.getHour();
            LocalTime startTime = booking.getDate().toLocalTime();
            LocalTime endTime = booking.getEndTime().toLocalTime();
            if((hour.isBefore(endTime) && hour.isAfter(startTime)) || hour.equals(startTime)){
                bookableHour.setBooked(false);
                if(bookableDate.isFullyBooked()){
                    bookableDate.setFullyBooked(false);
                }
            }
        }
        bookableDateService.saveBookableDate(bookableDate);
    }

    public void deleteBookings(List<Booking> bookings){
        for(Booking booking: bookings){
            setBookableHoursRelatedToBookingToAvailable(booking);
        }
        bookingRepo.deleteAll(bookings);
    }

    public void deleteAllBookings(){
        bookingRepo.deleteAll();
    }

    public boolean checkIfBookingOverlapsWithAlreadyBookedHours(LocalDateTime startTime, LocalDateTime endTime){
        return !bookingRepo.findByDateBetween(startTime, endTime.minusMinutes(1)).isEmpty() ||
                !bookingRepo.findByEndTimeBetween(startTime.plusMinutes(1), endTime).isEmpty();
    }

    public List<Booking> getBookingsBetweenTwoGivenDates(LocalDateTime fromDate, LocalDateTime toDate){
        return bookingRepo.findByDateBetween(fromDate, toDate);
    }

    public BookingWithoutIdDto convertBookingCustomerDepositTimeDtoToBookingWithoutIdDto(
            BookingCustomerDepositTimeDto givenBookingDto){
        Booking b = bookingRepo.findByCustomerAndDate(
                givenBookingDto.getCustomer(), givenBookingDto.getDate()).orElse(null);

        if(b == null){
            return null;
        }
        return BookingWithoutIdDto.builder()
                .depositPaid(b.isDepositPaid())
                .touchUp(b.isTouchUp())
                .finalPrice(b.getFinalPrice())
                .date(b.getDate())
                .notes(b.getNotes())
                .tattooImage(b.getTattooImage())
                .customer(b.getCustomer())
                .build();
    }

    @Transactional
    public Booking uploadTattooImage(UUID bookingId, MultipartFile file, List<UUID> categoryIds) {
        Booking booking = getBookingById(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("Booking not found");
        }

        if (categoryIds == null || categoryIds.isEmpty()) {
            throw new IllegalArgumentException("At least one category must be selected");
        }

        String imageUrl;
        try {
            imageUrl = s3ImageService.uploadImage(file, "Tattoos");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<ImageCategory> selectedCategories = imageCategoryService.getCategoriesByIds(categoryIds);

        TattooImage tattooImage = TattooImage.builder()
                .name(file.getOriginalFilename())
                .contentType(file.getContentType())
                .size(file.getSize())
                .url(imageUrl)
                .categories(selectedCategories)
                .booking(booking)
                .build();

        tattooImageService.saveTattooImage(tattooImage);

        booking.setTattooImage(tattooImage);
        saveBooking(booking);

        return booking;
    }

    @Transactional
    public Booking deleteTattooImage(UUID bookingId) {
        Booking booking = getBookingById(bookingId);
        if (booking == null) {
            throw new RuntimeException("Could not get booking");
        }

        TattooImage tattooImage = booking.getTattooImage();
        booking.setTattooImage(null);
        saveBooking(booking);

        s3ImageService.deleteImage(tattooImage.getUrl());

        tattooImageService.deleteTattooImage(tattooImage);

        return booking;
    }

}

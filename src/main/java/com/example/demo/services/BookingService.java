package com.example.demo.services;

import com.example.demo.model.BookableDate;
import com.example.demo.model.BookableHour;
import com.example.demo.model.Booking;
import com.example.demo.model.Customer;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
        return bookingRepo.findByDateBetween(
                        date.atStartOfDay(),
                        date.atTime(23, 59, 59))
                .stream()
                .sorted(Comparator.comparing(Booking::getDate))
                .toList();
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

    @Transactional
    public void deleteBooking(Booking booking){
        setBookableHoursRelatedToBookingToAvailable(booking);
        TattooImage tattooImage = booking.getTattooImage();

        if(tattooImage != null && tattooImage.getUrl() != null){
            s3ImageService.deleteImage(booking.getTattooImage().getUrl());
            tattooImageService.deleteTattooImage(tattooImage);
        }

        bookingRepo.delete(booking);
    }

    public void deleteFutureBookingsAndSetPastBookingsCustomerToNull(List<Booking> bookings){
        List<Booking> bookingsToSetCustomerToNull = new ArrayList<>();
        List<Booking> bookingsToDelete = new ArrayList<>();

        for(Booking booking: bookings){
            if(booking.getDate().isBefore(LocalDateTime.now())){
                s3ImageService.deleteImage(booking.getTattooImage().getUrl());
                tattooImageService.deleteTattooImage(booking.getTattooImage());
                booking.setCustomer(null);
                bookingsToSetCustomerToNull.add(booking);
            }else{
                bookingsToDelete.add(booking);
                setBookableHoursRelatedToBookingToAvailable(booking);
            }
        }

        if(!bookingsToSetCustomerToNull.isEmpty()){
            saveListOfBookings(bookingsToSetCustomerToNull);
        }

        if(!bookingsToDelete.isEmpty()){
            deleteBookings(bookingsToDelete);
        }
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
        List<TattooImage> tattooImages = new ArrayList<>();
        for(Booking booking: bookings){
            TattooImage tattooImage = booking.getTattooImage();
            if(tattooImage!= null){
                tattooImages.add(tattooImage);
                s3ImageService.deleteImage(tattooImage.getUrl());
            }
            setBookableHoursRelatedToBookingToAvailable(booking);
        }

        if(!tattooImages.isEmpty()){
            tattooImageService.deleteListOfTattooImages(tattooImages);
        }

        bookingRepo.deleteAll(bookings);
    }

    public boolean checkIfBookingOverlapsWithAlreadyBookedHours(LocalDateTime startTime, LocalDateTime endTime){
        return !bookingRepo.findByDateBetween(startTime, endTime.minusMinutes(1)).isEmpty() ||
                !bookingRepo.findByEndTimeBetween(startTime.plusMinutes(1), endTime).isEmpty();
    }

    public List<Booking> getBookingsBetweenTwoGivenDates(LocalDateTime fromDate, LocalDateTime toDate){
        return bookingRepo.findByDateBetween(fromDate, toDate);
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

    public List<Booking> sortBookingsByStartDateAndTime(List<Booking> bookings){
        return bookings.stream()
                .sorted(Comparator.comparing(Booking::getDate))
                .collect(Collectors.toList());
    }

    public String createBookingSuccessMessage(Customer customer, LocalTime startTime, LocalTime endTime, LocalDate date){
        return customer.getName() + " booked at " + startTime + " - " + endTime + " on " + date;
    }

}

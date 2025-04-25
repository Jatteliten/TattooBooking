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

    public void saveBooking(Booking booking){
        bookingRepo.save(booking);
    }

    public void saveListOfBookings(List<Booking> bookings){
        bookingRepo.saveAll(bookings);
    }

    public Booking getBookingById(UUID id){
        return bookingRepo.findById(id).orElse(null);
    }

    @Transactional
    public void deleteBooking(Booking booking){
        BookableDate bookableDate = setBookableHoursRelatedToBookingToAvailable(booking);
        if(bookableDate != null){
            bookableDateService.saveBookableDate(bookableDate);
        }
        TattooImage tattooImage = booking.getTattooImage();

        bookingRepo.delete(booking);

        if(tattooImage != null){
            String url = tattooImage.getUrl();
            tattooImageService.deleteTattooImage(tattooImage);
            if(url != null){
                s3ImageService.deleteImage(url);
            }
        }
    }

    public void deleteBookings(List<Booking> bookings){
        List<TattooImage> tattooImages = new ArrayList<>();
        List<String> tattooImageUrls = new ArrayList<>();
        List<BookableDate> bookableDates = new ArrayList<>();
        for(Booking booking: bookings){
            TattooImage tattooImage = booking.getTattooImage();
            if(tattooImage!= null){
                tattooImages.add(tattooImage);
                String url = tattooImage.getUrl();
                if(url != null){
                    tattooImageUrls.add(url);
                }
            }
            bookableDates.add(setBookableHoursRelatedToBookingToAvailable(booking));
        }

        bookingRepo.deleteAll(bookings);

        if(!bookableDates.isEmpty()){
            bookableDateService.saveListOfBookableDates(bookableDates);
        }
        if(!tattooImages.isEmpty()){
            tattooImageService.deleteListOfTattooImages(tattooImages);
            if(!tattooImageUrls.isEmpty()){
                s3ImageService.deleteImages(tattooImageUrls);
            }
        }
    }

    @Transactional
    public void deleteFutureBookingsAndSetPastBookingsToNull(List<Booking> bookings){
        List<Booking> bookingsToSetCustomerToNull = new ArrayList<>();
        List<Booking> bookingsToDelete = new ArrayList<>();
        List<BookableDate> bookableDatesToUpdate = new ArrayList<>();
        List<TattooImage> tattooImagesToDelete = new ArrayList<>();
        List<String> urlsToDeleteFromS3 = new ArrayList<>();

        for(Booking booking: bookings){
            if(booking.getDate().isBefore(LocalDateTime.now())){
                TattooImage tattooImage = booking.getTattooImage();
                if(tattooImage != null){
                    tattooImagesToDelete.add(tattooImage);
                    String url = tattooImage.getUrl();
                    if(url != null){
                        urlsToDeleteFromS3.add(url);
                    }
                }
                booking.setCustomer(null);
                bookingsToSetCustomerToNull.add(booking);
            }else{
                bookingsToDelete.add(booking);
                bookableDatesToUpdate.add(setBookableHoursRelatedToBookingToAvailable(booking));
            }
        }

        if(!bookableDatesToUpdate.isEmpty()){
            bookableDateService.saveListOfBookableDates(bookableDatesToUpdate);
        }
        if(!bookingsToSetCustomerToNull.isEmpty()){
            saveListOfBookings(bookingsToSetCustomerToNull);
        }
        if(!bookingsToDelete.isEmpty()){
            deleteBookings(bookingsToDelete);
        }
        if(!tattooImagesToDelete.isEmpty()){
            tattooImageService.deleteListOfTattooImages(tattooImagesToDelete);
        }
        if(!urlsToDeleteFromS3.isEmpty()){
            s3ImageService.deleteImages(urlsToDeleteFromS3);
        }
    }

    public List<Booking> getBookingsByDate(LocalDate date){
        return bookingRepo.findByDateBetween(date.atStartOfDay(), date.atTime(23, 59, 59));
    }

    public List<Booking> getBookingsBetweenTwoGivenDates(LocalDateTime fromDate, LocalDateTime toDate){
        return bookingRepo.findByDateBetween(fromDate, toDate);
    }

    public BookableDate setBookableHoursRelatedToBookingToAvailable(Booking booking){
        BookableDate bookableDate = bookableDateService.getBookableDateByDate(LocalDate.from(booking.getDate()));
        if(bookableDate != null) {
            List<BookableHour> bookableHoursOnDate = bookableDate.getBookableHours();

            for (BookableHour bookableHour : bookableHoursOnDate) {
                LocalTime hour = bookableHour.getHour();
                LocalTime startTime = booking.getDate().toLocalTime();
                LocalTime endTime = booking.getEndTime().toLocalTime();
                if ((hour.isBefore(endTime) && hour.isAfter(startTime)) || hour.equals(startTime)) {
                    bookableHour.setBooked(false);
                    if (bookableDate.isFullyBooked()) {
                        bookableDate.setFullyBooked(false);
                    }
                }
            }
        }
        return bookableDate;
    }

    public boolean checkIfBookingOverlapsWithAlreadyBookedHours(LocalDateTime startTime, LocalDateTime endTime){
        return !bookingRepo.findByDateBetween(startTime.plusMinutes(1), endTime.minusMinutes(1)).isEmpty() ||
                !bookingRepo.findByEndTimeBetween(startTime.plusMinutes(1), endTime.minusMinutes(1)).isEmpty();
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
    public void deleteTattooImageFromBooking(Booking booking) {
        TattooImage tattooImage = booking.getTattooImage();
        if (tattooImage == null) {
            throw new RuntimeException("Booking does not have a tattoo image.");
        }

        booking.setTattooImage(null);
        s3ImageService.deleteImage(tattooImage.getUrl());
        tattooImageService.deleteTattooImage(tattooImage);
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

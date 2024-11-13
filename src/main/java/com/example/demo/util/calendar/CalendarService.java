package com.example.demo.util.calendar;

import com.example.demo.services.BookingService;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalendarService {
    private final BookingService bookingService;

    public CalendarService(BookingService bookingService){
        this.bookingService = bookingService;
    }

    public List<List<CalendarDate>> getNextTwentyEightDates() {
        List<List<CalendarDate>> weeks = new ArrayList<>();
        List<CalendarDate> currentWeek = new ArrayList<>();

        LocalDate startDate = LocalDate.now();
        while (startDate.getDayOfWeek() != DayOfWeek.MONDAY) {
            startDate = startDate.minusDays(1);
        }

        for (int i = 0; i < 28; i++) {
            LocalDate currentDate = startDate.plusDays(i);
            CalendarDate calendarDate = CalendarDate.builder()
                    .date(currentDate)
                    .build();

            if (bookingService.getBookingsByDate(currentDate.atStartOfDay()).isEmpty()) {
                calendarDate.setColor("green");
            } else {
                calendarDate.setColor("red");
            }

            currentWeek.add(calendarDate);

            if (currentWeek.size() == 7) {
                weeks.add(new ArrayList<>(currentWeek));
                currentWeek.clear();
            }
        }

        return weeks;
    }
}

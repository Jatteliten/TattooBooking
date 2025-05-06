package com.example.tattooplatform.services;

import com.example.tattooplatform.dto.bookabledate.BookableDateCalendarDto;
import com.example.tattooplatform.dto.bookablehour.BookableHourCalendarDto;
import com.example.tattooplatform.model.BookableDate;
import com.example.tattooplatform.model.BookableHour;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CalendarService {
    private final BookableDateService bookableDateService;

    public CalendarService(BookableDateService bookableDateService){
        this.bookableDateService = bookableDateService;
    }

    public void createCalendarModel(Model model, Integer year, Integer month){
        LocalDate today = LocalDate.now();
        LocalDate selectedDate = LocalDate.of(
                (year != null) ? year : today.getYear(),
                (month != null) ? month : today.getMonthValue(),
                1
        );

        List<BookableDateCalendarDto> days = createDaysInMonthFromSelectedDate(selectedDate);

        model.addAttribute("days", days);
        model.addAttribute("month", selectedDate.getMonthValue());
        model.addAttribute("year", selectedDate.getYear());
        model.addAttribute("prevMonth", selectedDate.minusMonths(1).getMonthValue());
        model.addAttribute("prevYear", selectedDate.minusMonths(1).getYear());
        model.addAttribute("nextMonth", selectedDate.plusMonths(1).getMonthValue());
        model.addAttribute("nextYear", selectedDate.plusMonths(1).getYear());
    }

    public List<BookableDateCalendarDto> createDaysInMonthFromSelectedDate(LocalDate selectedDate) {
        LocalDate firstDayOfMonth = selectedDate.withDayOfMonth(1);
        LocalDate firstDayOfCalendar = firstDayOfMonth.with(DayOfWeek.MONDAY);
        LocalDate lastDayOfMonth = selectedDate.withDayOfMonth(selectedDate.lengthOfMonth());
        LocalDate lastDayOfCalendar = lastDayOfMonth.with(DayOfWeek.SUNDAY);
        List<BookableDateCalendarDto> days = new ArrayList<>();
        LocalDate date = firstDayOfCalendar;

        Map<LocalDate, BookableDateCalendarDto> dateToBookableDateForCalendarDto =
                convertListOfBookableDatesToBookableDateCalendarDto(
                        bookableDateService.getBookableDatesBetweenTwoDates(
                                firstDayOfMonth, lastDayOfMonth))
                        .stream()
                        .collect(Collectors.toMap(BookableDateCalendarDto::getDate, dto -> dto));

        while (!date.isAfter(lastDayOfCalendar)) {
            boolean isCurrentMonth = date.getMonth() == selectedDate.getMonth();
            BookableDateCalendarDto bookableDate = dateToBookableDateForCalendarDto.get(date);
            if(date.isAfter(LocalDate.now().minusDays(1)) && bookableDate != null){
                bookableDate.setCurrentMonth(isCurrentMonth);
                if(date.isBefore(LocalDate.now()) && !bookableDate.isDropIn()){
                    bookableDate.setFullyBooked(true);
                    bookableDate.getHours().clear();
                }
                days.add(bookableDate);
            }else{
                BookableDateCalendarDto bookableDateCalendarDto = BookableDateCalendarDto.builder()
                        .currentMonth(isCurrentMonth)
                        .bookable(false)
                        .date(date)
                        .build();
                days.add(bookableDateCalendarDto);
            }
            date = date.plusDays(1);
        }
        return days;
    }

    public BookableDateCalendarDto convertBookableDateToBookableDateCalendarDto(BookableDate bookableDate){
        List<String> bookableHoursStringsForCalendarList = new ArrayList<>();

        for(BookableHour bookableHour: bookableDate.getBookableHours()){
            BookableHourCalendarDto bookableHourCalendarDto =
                    convertBookableHourToBookableHourCalendarDto(bookableHour);
            bookableHoursStringsForCalendarList.add(bookableHourCalendarDto.getHour() + "-"
                    + bookableHourCalendarDto.isBooked());
        }

        Collections.sort(bookableHoursStringsForCalendarList);

        return BookableDateCalendarDto.builder()
                .date(bookableDate.getDate())
                .bookable(true)
                .hours(bookableHoursStringsForCalendarList)
                .currentMonth(true)
                .fullyBooked(bookableDate.isFullyBooked())
                .dropIn(bookableDate.isDropIn())
                .touchUp(bookableDate.isTouchUp())
                .build();
    }

    public List<BookableDateCalendarDto> convertListOfBookableDatesToBookableDateCalendarDto(
            List<BookableDate> bookableDateList){
        return bookableDateList.stream()
                .map(this::convertBookableDateToBookableDateCalendarDto)
                .toList();
    }

    public BookableHourCalendarDto convertBookableHourToBookableHourCalendarDto(BookableHour bookableHour){
        return BookableHourCalendarDto.builder()
                .hour(bookableHour.getHour())
                .booked(bookableHour.isBooked())
                .build();
    }
}

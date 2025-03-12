package com.project.eventManagement.controller;

import com.project.eventManagement.dto.request.BookingRequest;
import com.project.eventManagement.dto.response.BookingResponse;
import com.project.eventManagement.dto.response.EventResponse;
import com.project.eventManagement.model.Booking;
import com.project.eventManagement.model.Event;
import com.project.eventManagement.service.BookingService;
import com.project.eventManagement.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;


import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {
    private final EventService eventService;
    private final BookingService bookingService;

    public UserController(EventService eventService, BookingService bookingService){
        this.bookingService = bookingService;
        this.eventService = eventService;
    }

    @GetMapping("/events")
    public ResponseEntity<List<EventResponse>> getAvailableEvents() {
        List<Event> events = eventService.getAvailableEvents();
        return ResponseEntity.ok(eventService.convertToEventResponseList(events));
    }

    @PostMapping("/events/{id}/book")
    public ResponseEntity<BookingResponse> bookEvent(@PathVariable Long id, @RequestBody BookingRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Booking booking = bookingService.bookEvent(id, request, username);
        return new ResponseEntity<>(bookingService.convertToBookingResponse(booking), HttpStatus.CREATED);
    }

    @DeleteMapping("/events/{id}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        bookingService.cancelBooking(id, username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/events/history")
    public ResponseEntity<List<BookingResponse>> getBookingHistory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        List<Booking> bookings = bookingService.getBookingHistoryByUser(username);
        return ResponseEntity.ok(bookingService.convertToBookingResponseList(bookings));
    }
}

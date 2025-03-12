package com.project.eventManagement.controller;

import com.project.eventManagement.dto.request.EventRequest;
import com.project.eventManagement.dto.response.BookingResponse;
import com.project.eventManagement.dto.response.EventResponse;
import com.project.eventManagement.model.Booking;
import com.project.eventManagement.model.Event;
import com.project.eventManagement.service.BookingService;
import com.project.eventManagement.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final EventService eventService;
    private final BookingService bookingService;

    public AdminController(EventService eventService, BookingService bookingService){
        this.bookingService = bookingService;
        this.eventService = eventService;
    }

    @PostMapping("/events")
    public ResponseEntity<EventResponse> createEvent(@RequestBody EventRequest request) {
        Event event = eventService.createEvent(request);
        return new ResponseEntity<>(eventService.convertToEventResponse(event), HttpStatus.CREATED);
    }

    @PutMapping("/events/{id}")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable Long id, @RequestBody EventRequest request) {
        Event event = eventService.eventUpdate(id, request);
        return ResponseEntity.ok(eventService.convertToEventResponse(event));
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/events")
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        List<Event> events = eventService.getAllEvents();
        return ResponseEntity.ok(eventService.convertToEventResponseList(events));
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookingService.convertToBookingResponseList(bookings));
    }

}

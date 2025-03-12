package com.project.eventManagement.service;

import com.project.eventManagement.dto.request.BookingRequest;
import com.project.eventManagement.dto.response.BookingResponse;
import com.project.eventManagement.exception.ResourceNotFoundException;
import com.project.eventManagement.model.Booking;
import com.project.eventManagement.model.Event;
import com.project.eventManagement.model.User;
import com.project.eventManagement.repository.BookingRepository;
import com.project.eventManagement.repository.EventRepository;
import com.project.eventManagement.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository, EventRepository eventRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Booking bookEvent(Long eventId, BookingRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));

        if (event.getAvailableSeats() < request.getNumberOfTickets()) {
            throw new IllegalArgumentException("Not enough seats available");
        }

        if (event.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Event has already started or ended");
        }

        event.setAvailableSeats(event.getAvailableSeats() - request.getNumberOfTickets());
        eventRepository.save(event);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setEvent(event);
        booking.setNumberOfTickets(request.getNumberOfTickets());
        booking.setTotalAmount(event.getTicketPrice() * request.getNumberOfTickets());
        booking.setBookingTime(LocalDateTime.now());
        booking.setStatus("CONFIRMED");

        return bookingRepository.save(booking);
    }

    @Transactional
    public void cancelBooking(Long bookingId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        Booking booking = bookingRepository.findByIdAndUser(bookingId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (booking.getStatus().equals("CANCELLED")) {
            throw new IllegalArgumentException("Booking is already cancelled");
        }

        if (booking.getEvent().getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot cancel booking for an event that has already started");
        }

        Event event = booking.getEvent();
        event.setAvailableSeats(event.getAvailableSeats() + booking.getNumberOfTickets());
        eventRepository.save(event);

        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
    }

    public List<Booking> getBookingHistoryByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        return bookingRepository.findByUser(user);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public BookingResponse convertToBookingResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getEvent().getId(),
                booking.getEvent().getName(),
                booking.getEvent().getStartTime(),
                booking.getEvent().getLocation(),
                booking.getNumberOfTickets(),
                booking.getTotalAmount(),
                booking.getBookingTime(),
                booking.getStatus()
        );
    }

    public List<BookingResponse> convertToBookingResponseList(List<Booking> bookings) {
        return bookings.stream()
                .map(this::convertToBookingResponse)
                .collect(Collectors.toList());
    }
}

package com.project.eventManagement;

import com.project.eventManagement.dto.request.BookingRequest;
import com.project.eventManagement.model.Booking;
import com.project.eventManagement.model.Event;
import com.project.eventManagement.model.User;
import com.project.eventManagement.repository.BookingRepository;
import com.project.eventManagement.repository.EventRepository;
import com.project.eventManagement.repository.UserRepository;
import com.project.eventManagement.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    private BookingRequest bookingRequest;
    private Booking booking;

    @BeforeEach
    public void setup() {
        bookingRequest = BookingRequest.builder()
                .numberOfTickets(5)
                .build();

        booking = Booking.builder()
                .id(1L)
                .numberOfTickets(5)
                .totalAmount(250.0)
                .bookingTime(LocalDateTime.now())
                .status("CONFIRMED")
                .build();
    }

    @Test
    public void testBookEvent() {
        User user = new User();
        user.setUsername("testUser");
        given(userRepository.findByUsername("testUser")).willReturn(Optional.of(user));

        Event event = new Event();
        event.setAvailableSeats(10);
        event.setTicketPrice(50.0);
        event.setStartTime(LocalDateTime.now().plusDays(1));
        given(eventRepository.findById(1L)).willReturn(Optional.of(event));
        given(eventRepository.save(any(Event.class))).willReturn(event);
        given(bookingRepository.save(any(Booking.class))).willReturn(booking);

        Booking result = bookingService.bookEvent(1L, bookingRequest, "testUser");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNumberOfTickets()).isEqualTo(5);
        assertThat(result.getTotalAmount()).isEqualTo(250.0);
        assertThat(result.getStatus()).isEqualTo("CONFIRMED");
    }

    @Test
    public void testBookEvent_NotEnoughSeats() {
        User user = new User();
        user.setUsername("testUser");
        given(userRepository.findByUsername("testUser")).willReturn(Optional.of(user));

        Event event = new Event();
        event.setAvailableSeats(3);
        event.setTicketPrice(50.0);
        event.setStartTime(LocalDateTime.now().plusDays(1));
        given(eventRepository.findById(1L)).willReturn(Optional.of(event));

        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.bookEvent(1L, bookingRequest, "testUser");
        });
    }

    @Test
    public void testBookEvent_EventAlreadyStarted() {
        User user = new User();
        user.setUsername("testUser");
        given(userRepository.findByUsername("testUser")).willReturn(Optional.of(user));

        Event event = new Event();
        event.setAvailableSeats(10);
        event.setTicketPrice(50.0);
        event.setStartTime(LocalDateTime.now().minusDays(1));
        given(eventRepository.findById(1L)).willReturn(Optional.of(event));

        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.bookEvent(1L, bookingRequest, "testUser");
        });
    }

    @Test
    public void getBookingHistoryByUserTest() {
        User user = new User();
        user.setUsername("testUser");
        given(userRepository.findByUsername("testUser")).willReturn(Optional.of(user));

        given(bookingRepository.findByUser(user)).willReturn(List.of(booking));

        List<Booking> result = bookingService.getBookingHistoryByUser("testUser");

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.getFirst().getId()).isEqualTo(1L);
        assertThat(result.getFirst().getNumberOfTickets()).isEqualTo(5);
        assertThat(result.getFirst().getTotalAmount()).isEqualTo(250.0);
        assertThat(result.getFirst().getStatus()).isEqualTo("CONFIRMED");
    }

    @Test
    public void getAllBookingsTest() {
        given(bookingRepository.findAll()).willReturn(List.of(booking, booking));

        List<Booking> result = bookingService.getAllBookings();
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void testCancelBooking() {
        User user = new User();
        user.setUsername("testUser");
        given(userRepository.findByUsername("testUser")).willReturn(Optional.of(user));

        Event event = new Event();
        event.setStartTime(LocalDateTime.now().plusDays(1));
        event.setAvailableSeats(5);
        given(eventRepository.save(any(Event.class))).willReturn(event);

        booking.setUser(user);
        booking.setEvent(event);
        booking.setNumberOfTickets(5);
        booking.setStatus("CONFIRMED");

        given(bookingRepository.findByIdAndUser(1L, user)).willReturn(Optional.of(booking));
        given(bookingRepository.save(any(Booking.class))).willReturn(booking);

        bookingService.cancelBooking(1L, "testUser");

        assertThat(booking.getStatus()).isEqualTo("CANCELLED");
        assertThat(booking.getEvent().getAvailableSeats()).isEqualTo(10);
    }

    @Test
    public void testCancelBooking_AlreadyCancelled() {
        User user = new User();
        user.setUsername("testUser");
        given(userRepository.findByUsername("testUser")).willReturn(Optional.of(user));

        Event event = new Event();
        event.setStartTime(LocalDateTime.now().plusDays(1));

        booking.setUser(user);
        booking.setEvent(event);
        booking.setStatus("CANCELLED");

        given(bookingRepository.findByIdAndUser(1L, user)).willReturn(Optional.of(booking));

        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.cancelBooking(1L, "testUser");
        });
    }

    @Test
    public void testCancelBooking_EventAlreadyStarted() {
        User user = new User();
        user.setUsername("testUser");
        given(userRepository.findByUsername("testUser")).willReturn(Optional.of(user));

        Event event = new Event();
        event.setStartTime(LocalDateTime.now().minusDays(1));

        booking.setUser(user);
        booking.setEvent(event);
        booking.setStatus("CONFIRMED");

        given(bookingRepository.findByIdAndUser(1L, user)).willReturn(Optional.of(booking));

        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.cancelBooking(1L, "testUser");
        });
    }

}

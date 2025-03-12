package com.project.eventManagement;

import com.project.eventManagement.dto.request.EventRequest;
import com.project.eventManagement.dto.response.EventResponse;
import com.project.eventManagement.exception.ResourceNotFoundException;
import com.project.eventManagement.model.Event;
import com.project.eventManagement.repository.EventRepository;
import com.project.eventManagement.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    private EventRequest eventRequest;
    private Event event;

    @BeforeEach
    public void setup() {
        eventRequest = EventRequest.builder()
                .name("Test Event")
                .description("Test Description")
                .startTime(LocalDateTime.parse("2025-06-20T14:30:00"))
                .endTime(LocalDateTime.parse("2025-06-20T15:30:00"))
                .location("New Delhi")
                .totalSeats(100)
                .ticketPrice(50.0)
                .build();

        event = Event.builder()
                .id(1L)
                .name("Test Event")
                .description("Test Description")
                .startTime(LocalDateTime.parse("2025-06-20T14:30:00"))
                .endTime(LocalDateTime.parse("2025-06-20T15:30:00"))
                .location("New Delhi")
                .totalSeats(100)
                .availableSeats(100)
                .ticketPrice(50.0)
                .build();
    }

    @Test
    public void createEventTest() {
        given(eventRepository.save(any(Event.class))).willReturn(event);

        Event createdEvent = eventService.createEvent(eventRequest);

        assertThat(createdEvent).isNotNull();
        assertThat(createdEvent.getName()).isEqualTo(eventRequest.getName());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    public void eventUpdateTest() {
        given(eventRepository.findById(event.getId())).willReturn(Optional.of(event));
        given(eventRepository.save(any(Event.class))).willReturn(event);

        Event updatedEvent = eventService.eventUpdate(event.getId(), eventRequest);

        assertThat(updatedEvent).isNotNull();
        assertThat(updatedEvent.getName()).isEqualTo(eventRequest.getName());
        verify(eventRepository, times(1)).findById(event.getId());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    public void deleteEventTest() {
        given(eventRepository.findById(event.getId())).willReturn(Optional.of(event));
        doNothing().when(eventRepository).delete(event);

        eventService.deleteEvent(event.getId());

        verify(eventRepository, times(1)).findById(event.getId());
        verify(eventRepository, times(1)).delete(event);
    }

    @Test
    public void getAllEventsTest() {
        List<Event> events = Collections.singletonList(event);
        given(eventRepository.findAll()).willReturn(events);

        List<Event> allEvents = eventService.getAllEvents();

        assertThat(allEvents).isNotNull();
        assertThat(allEvents.size()).isEqualTo(1);
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    public void getEventByIdTest() {
        given(eventRepository.findById(event.getId())).willReturn(Optional.of(event));

        Event foundEvent = eventService.getEventById(event.getId());

        assertThat(foundEvent).isNotNull();
        assertThat(foundEvent.getId()).isEqualTo(event.getId());
        verify(eventRepository, times(1)).findById(event.getId());
    }

    @Test
    public void getEventByIdNotFoundTest() {
        given(eventRepository.findById(event.getId())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            eventService.getEventById(event.getId());
        });

        verify(eventRepository, times(1)).findById(event.getId());
    }

    @Test
    public void convertToEventResponseTest() {
        EventResponse eventResponse = eventService.convertToEventResponse(event);

        assertThat(eventResponse).isNotNull();
        assertThat(eventResponse.getName()).isEqualTo(event.getName());
    }

    @Test
    public void convertToEventResponseListTest() {
        List<Event> events = Collections.singletonList(event);
        List<EventResponse> eventResponses = eventService.convertToEventResponseList(events);

        assertThat(eventResponses).isNotNull();
        assertThat(eventResponses.size()).isEqualTo(1);
        assertThat(eventResponses.getFirst().getName()).isEqualTo(event.getName());
    }


    @Test
    public void getAvailableEventsTest() {
        List<Event> events = new ArrayList<>();
        events.add(event);
        given(eventRepository.findUpcomingEvents(any(LocalDateTime.class))).willReturn(events);

        List<Event> availableEvents = eventService.getAvailableEvents();


        assertThat(availableEvents).isNotNull();
        assertThat(availableEvents.size()).isEqualTo(1);
        verify(eventRepository, times(1)).findUpcomingEvents(any(LocalDateTime.class));
    }

}
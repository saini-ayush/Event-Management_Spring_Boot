package com.project.eventManagement.service;

import com.project.eventManagement.dto.request.EventRequest;
import com.project.eventManagement.dto.response.EventResponse;
import com.project.eventManagement.exception.ResourceNotFoundException;
import com.project.eventManagement.model.Event;
import com.project.eventManagement.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository){
        this.eventRepository=eventRepository;
    }

    public Event createEvent(EventRequest request){
        Event event = new Event();
        event.setName(request.getName());
        event.setDescription(request.getDescription());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        event.setLocation(request.getLocation());
        event.setTotalSeats(request.getTotalSeats());
        event.setAvailableSeats(request.getTotalSeats());
        event.setTicketPrice(request.getTicketPrice());

        return eventRepository.save(event);
    }

    public Event eventUpdate(Long id, EventRequest request){
        Event event = eventRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Resource not available"));

        event.setName(request.getName());
        event.setDescription(request.getDescription());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        event.setLocation(request.getLocation());

        // have to check later as there is an issue with this have to check seats before reducing
        Integer additionalSeats = request.getTotalSeats() - event.getTotalSeats();
        event.setTotalSeats(request.getTotalSeats());
        event.setAvailableSeats(event.getAvailableSeats() + additionalSeats);

        event.setTicketPrice(request.getTicketPrice());

        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));

        eventRepository.delete(event);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> getAvailableEvents() {
        return eventRepository.findUpcomingEvents(LocalDateTime.now());
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
    }

    public EventResponse convertToEventResponse(Event event) {
        return new EventResponse(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getStartTime(),
                event.getEndTime(),
                event.getLocation(),
                event.getTotalSeats(),
                event.getAvailableSeats(),
                event.getTicketPrice()
        );
    }

    public List<EventResponse> convertToEventResponseList(List<Event> events) {
        return events.stream()
                .map(this::convertToEventResponse)
                .collect(Collectors.toList());
    }

}

package com.project.eventManagement.repository;

import com.project.eventManagement.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStartTimeAfterAndAvailableSeatsGreaterThan(LocalDateTime now, Integer minSeats);

    @Query("SELECT e FROM Event e WHERE e.startTime > :now ORDER BY e.startTime ASC")
    List<Event> findUpcomingEvents(LocalDateTime now);
}

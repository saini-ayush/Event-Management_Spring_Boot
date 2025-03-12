package com.project.eventManagement.repository;

import com.project.eventManagement.model.Booking;
import com.project.eventManagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser(User user);
    List<Booking> findByUserAndStatus(User user, String status);
    Optional<Booking> findByIdAndUser(Long id, User user);
}

package com.project.eventManagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private Long eventId;
    private String eventName;
    private LocalDateTime eventStartTime;
    private String location;
    private Integer numberOfTickets;
    private Double totalAmount;
    private LocalDateTime bookingTime;
    private String status;
}

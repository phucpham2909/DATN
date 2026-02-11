package com.ht_cinema.movieticket.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    private String ticketId;
    private Movie movie;
    private List<String> seats;
    private String cinema;
    private String showtime;
    private String bookingTime;
    private String status;
}

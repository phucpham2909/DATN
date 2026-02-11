package com.ht_cinema.movieticket.model;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingInfo {
    private Movie movie;
	private List<String> seats;
    private double pricePerSeat;
    private double total;
    private String cinema;
    private String showtime;
}

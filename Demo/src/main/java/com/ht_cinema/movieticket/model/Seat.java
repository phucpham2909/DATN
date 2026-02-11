package com.ht_cinema.movieticket.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seat {
	private String id;
	private double price;
	private boolean booked;
}

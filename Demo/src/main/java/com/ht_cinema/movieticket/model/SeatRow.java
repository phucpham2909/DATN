package com.ht_cinema.movieticket.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatRow {
	private String rowLabel;
	private List<Seat> seats;
}

package com.ht_cinema.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingAdminDTO {
	private Integer bookingId;
	private String username;
	private String filmName;
	private String showTime;
	private String cinemaName;
	private String roomName;

	private String seatNames;
	private Integer seatTotal;

	private String comboNames;
	private Integer comboTotal;

	private Integer grandTotal;
}

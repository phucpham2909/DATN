package com.ht_cinema.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ht_cinema.model.Rooms;
import com.ht_cinema.model.Seats;
import com.ht_cinema.repository.SeatsRepository;

@Service
public class SeatService {

	@Autowired
	private SeatsRepository seatsRepository;

	public void createSeatsForRoom(Rooms room, int basePrice) {
		List<Seats> seats = new ArrayList<>();
		int rows = 10;
		int cols = 15;

		Set<String> existingNames = room.getSeats().stream().map(Seats::getName).collect(Collectors.toSet());

		for (int r = 0; r < rows; r++) {
			char rowChar = (char) ('A' + r);
			for (int c = 1; c <= cols; c++) {
				String seatName = rowChar + "" + c;

				if (existingNames.contains(seatName)) {
					continue; // bỏ qua ghế đã tồn tại
				}

				Seats seat = new Seats();
				seat.setRoom(room);
				seat.setName(seatName);

				int type = r < 3 ? 0 : 1;
				seat.setType(type);

				int price = type == 0 ? basePrice : (int) (basePrice * 1.2);
				seat.setPrice(price);
				seat.setStatus(0);

				seats.add(seat);
			}
		}

		seatsRepository.saveAll(seats);
	}

	public void updatePricesForRoom(Rooms room, int basePrice, double vipPercent) {
		List<Seats> seats = seatsRepository.findByRoomOrderById(room);
		double vipMultiplier = 1 + vipPercent / 100.0;

		for (Seats seat : seats) {
			if (seat.getType() == 1) {
				seat.setPrice((int) (basePrice * vipMultiplier));
			} else {
				seat.setPrice(basePrice);
			}
		}

		seatsRepository.saveAll(seats);
	}

	public void recreateMissingSeats(Rooms room, int basePrice) {
		List<Seats> existingSeats = seatsRepository.findByRoomOrderById(room);

		Set<String> existingNames = existingSeats.stream().map(Seats::getName).collect(Collectors.toSet());

		int totalSeats = 150;
		for (int i = 1; i <= totalSeats; i++) {
			String seatName = "A" + i;
			if (!existingNames.contains(seatName)) {
				Seats seat = new Seats();
				seat.setName(seatName);
				seat.setRoom(room);
				seat.setPrice(basePrice);
				seat.setType(0);
				seat.setStatus(0);
				seatsRepository.save(seat);
			}
		}
	}

	public void recreateSeatsForRoom(Rooms room, int basePrice) {
		int rows = 10;
		int cols = 15;

		Set<String> existingNames = room.getSeats().stream().map(Seats::getName).collect(Collectors.toSet());

		for (int i = 0; i < rows; i++) {
			char rowChar = (char) ('A' + i);
			for (int j = 1; j <= cols; j++) {
				String seatName = rowChar + String.valueOf(j);

				if (existingNames.contains(seatName)) {
					continue; // bỏ qua ghế đã tồn tại
				}

				Seats seat = new Seats();
				seat.setRoom(room);
				seat.setName(seatName);
				seat.setType(0);
				seat.setPrice(basePrice);
				seat.setStatus(0);
				seatsRepository.save(seat);
			}
		}
	}

}

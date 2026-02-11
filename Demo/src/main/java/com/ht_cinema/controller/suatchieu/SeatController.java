package com.ht_cinema.controller.suatchieu;

import com.ht_cinema.model.Cinemas;
import com.ht_cinema.model.Rooms;
import com.ht_cinema.model.Seats;
import com.ht_cinema.repository.CinemasRepository;
import com.ht_cinema.repository.RoomsRepository;
import com.ht_cinema.repository.SeatsRepository;
import com.ht_cinema.service.SeatService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/seats")
public class SeatController {

	@Autowired
	private SeatsRepository seatsRepository;

	@Autowired
	private RoomsRepository roomsRepository;

	@Autowired
	private SeatService seatService;

	@Autowired
	private CinemasRepository cinemasRepository;

	@GetMapping("/dashboard")
	public String dashboard(@RequestParam(required = false) Integer cinemaId, Model model) {
		List<Cinemas> cinemas = cinemasRepository.findAll();
		model.addAttribute("activeParent", "cinema");
		model.addAttribute("activePage", "seats");
		model.addAttribute("cinemas", cinemas);

		if (cinemaId != null) {
			Cinemas selectedCinema = cinemasRepository.findById(cinemaId)
					.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chi nhánh"));
			model.addAttribute("selectedCinema", selectedCinema);
			model.addAttribute("rooms", selectedCinema.getRooms());
		}

		return "admin/suatchieu/seat_dashboard";
	}

	@GetMapping("/room/{roomId}")
	public String listSeatsByRoom(@PathVariable Integer roomId, @RequestParam(required = false) Integer cinemaId,
			Model model) {
		Rooms room = roomsRepository.findById(roomId)
				.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng"));

		// Lấy TẤT CẢ ghế (bao gồm status = -1) để Admin quản lý
		List<Seats> seats = seatsRepository.findByRoomOrderById(room);
		
		// Kiểm tra xem có ghế HỎNG/ẨN (status = -1) hay không để hiển thị nút "Tạo lại ghế"
		boolean hasBrokenSeats = seats.stream().anyMatch(seat -> seat.getStatus() == -1);

		model.addAttribute("room", room);
		model.addAttribute("seats", seats);
		model.addAttribute("cinemaId", cinemaId);
		model.addAttribute("hasBrokenSeats", hasBrokenSeats);

		model.addAttribute("activeParent", "cinema");
		model.addAttribute("activePage", "seats");
		return "admin/suatchieu/seat_list";
	}

	@GetMapping("/create/{roomId}")
	public String createSeatsForRoom(@PathVariable Integer roomId, @RequestParam(required = false) Integer cinemaId) {
		Rooms room = roomsRepository.findById(roomId)
				.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng"));
		int basePrice = 50000;
		seatService.createSeatsForRoom(room, basePrice);

		return "redirect:/admin/seats/room/" + roomId + (cinemaId != null ? "?cinemaId=" + cinemaId : "");
	}

	@GetMapping("/edit/{id}")
	public String showEditForm(@PathVariable Integer id, @RequestParam(required = false) Integer cinemaId,
			Model model) {
		Seats seat = seatsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ghế"));
		model.addAttribute("seat", seat);
		model.addAttribute("cinemaId", cinemaId);
		model.addAttribute("activeParent", "cinema");
		model.addAttribute("activePage", "seats");
		return "admin/suatchieu/seat_edit";
	}

	@PostMapping("/edit")
	public String updateSeat(@ModelAttribute Seats seatForm, @RequestParam(required = false) Integer cinemaId) {
		Seats seat = seatsRepository.findById(seatForm.getId())
				.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ghế"));

		seat.setName(seatForm.getName());
		seat.setType(seatForm.getType());
		seat.setPrice(seatForm.getPrice());
		
		seat.setStatus(seatForm.getStatus()); 

		seatsRepository.save(seat);

		return "redirect:/admin/seats/room/" + seat.getRoom().getId()
				+ (cinemaId != null ? "?cinemaId=" + cinemaId : "");
	}

	@GetMapping("/recreate/{roomId}")
	@Transactional
	public String recreateSeats(@PathVariable Integer roomId) {
		Rooms room = roomsRepository.findById(roomId)
				.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng"));
		
		List<Seats> brokenSeats = seatsRepository.findByRoomAndStatus(room, -1);
		
		for (Seats brokenSeat : brokenSeats) {
			brokenSeat.setStatus(0); 
			seatsRepository.save(brokenSeat);
		}
		return "redirect:/admin/seats/room/" + roomId;
	}

	@PostMapping("/update-prices")
	public String updatePrices(@RequestParam Integer roomId, @RequestParam int basePrice,
			@RequestParam(defaultValue = "1.2") double vipMultiplier,
			@RequestParam(required = false) Integer cinemaId) {
		Rooms room = roomsRepository.findById(roomId)
				.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng"));
		seatService.updatePricesForRoom(room, basePrice, vipMultiplier);

		return "redirect:/admin/seats/room/" + roomId + (cinemaId != null ? "?cinemaId=" + cinemaId : "");
	}

	@GetMapping("/delete/{id}")
	public String deleteSeat(@PathVariable Integer id, @RequestParam(required = false) Integer cinemaId) {
		Seats seat = seatsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ghế"));
		
		seat.setStatus(-1); 
		seatsRepository.save(seat);
		
		Integer roomId = seat.getRoom().getId();
		return "redirect:/admin/seats/room/" + roomId + (cinemaId != null ? "?cinemaId=" + cinemaId : "");
	}

}
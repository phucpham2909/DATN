package com.ht_cinema.controller.admin;

import com.ht_cinema.model.Account;
import com.ht_cinema.model.Booking;
import com.ht_cinema.model.Ve;
import com.ht_cinema.repository.BookingRepository;
import com.ht_cinema.repository.VeRepository;
import com.ht_cinema.service.AdminBookingService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin/booking")
public class AdminBookingController {

	@Autowired
	private BookingRepository bookingRepo;

	@Autowired
	private VeRepository veRepo;
	 @Autowired
	    private AdminBookingService bookingService;
	@GetMapping("/history")
	public String bookingHistory(Model model, HttpSession session) {

		Account account = (Account) session.getAttribute("currentUser");
		if (account == null || !account.isRole()) {
			return "redirect:/login/form"; // nếu chưa login hoặc không phải admin
		}

		List<Booking> bookings = bookingRepo.findAll();
		model.addAttribute("bookings", bookings);

		return "admin/booking/history";
	}

	@GetMapping("/details")
	public String bookingDetails(@RequestParam Integer bookingId, Model model, HttpSession session) {

		Account account = (Account) session.getAttribute("currentUser");
		if (account == null || !account.isRole()) {
			return "redirect:/login/form";
		}

		Booking booking = bookingRepo.findById(bookingId)
				.orElseThrow(() -> new IllegalArgumentException("Booking not found"));

		List<Ve> veList = veRepo.findByBookingId(bookingId);

		model.addAttribute("booking", booking);
		model.addAttribute("veList", veList);

		return "admin/booking/details";
	}
	
	@GetMapping("/bookings")
	public String list(Model model) {
		model.addAttribute("bookings", bookingService.getAllBookingAdmin());
		model.addAttribute("activePage", "booking");
		return "admin/booking/list";
	}
}

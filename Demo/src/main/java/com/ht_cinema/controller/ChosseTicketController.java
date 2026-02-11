package com.ht_cinema.controller;


import java.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ChosseTicketController {

	@GetMapping("/film/chosseTickets")
	public String showBooking() {
		return "xuatchieu/tickets";
	}

	@PostMapping("/film/chosseTickets")
	public String confirmBooking(@RequestParam(required = false, name = "selectedSeats") List<String> selectedSeats,
			Model model) {

		model.addAttribute("selectedSeats", selectedSeats);
		return "film/confirm";
	}
}

package com.ht_cinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;

@Controller
public class ViewController {

	@GetMapping("/movie/{id}")
	public String movieDetail(@PathVariable int id) {
		return "movie/detail"; // => templates/movie/detail.html
	}

	@GetMapping("/home")
	public String home(Model model) {
		return "home/index2";
	}

	@GetMapping("/booking")
	public String bookingPage() {
		return "movie/seat";
	}

	@GetMapping("/checkout")
	public String showCheckoutPage() {
		return "movie/checkout";
	}
}

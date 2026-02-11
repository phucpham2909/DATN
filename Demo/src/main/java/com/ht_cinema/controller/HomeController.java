package com.ht_cinema.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ht_cinema.model.Film;
import com.ht_cinema.repository.FilmRepository;

@Controller
public class HomeController {

	@Autowired
	private FilmRepository filmRepository;

	@GetMapping("/")
	public String index(Model model) {
		List<Film> films = filmRepository.findTop12ByStatusOrderByIdDesc(1); // 1 = đang chiếu

		List<Film> carouselFilms = films.stream().filter(f -> f.getPoster() != null).limit(4).toList();

		List<Film> comingSoonFilms = filmRepository.findTop4ByStatusOrderByIdDesc(2);

		comingSoonFilms.forEach(f -> {
			if (f != null && f.getAvatar() != null) {
				if (!f.getAvatar().startsWith("http") && !f.getAvatar().startsWith("/film-images/")) {
					f.setAvatar("/film-images/" + f.getAvatar());
				}
			}
		});
		List<Film> topRevenueFilms = filmRepository.findTop3ByStatusOrderByDoanhThuDesc(1);

		// Xử lý đường dẫn ảnh 
		topRevenueFilms.forEach(f -> {
		    if (f != null && f.getAvatar() != null) {
		        if (!f.getAvatar().startsWith("http") && !f.getAvatar().startsWith("/film-images/")) {
		            f.setAvatar("/film-images/" + f.getAvatar());
		        }
		    }
		});

		model.addAttribute("topRevenueFilms", topRevenueFilms);

		System.out.println(">>> Tổng phim mới nhất: " + films.size());
		System.out.println(">>> Carousel phim: " + carouselFilms.size());
		System.out.println(">>> Phim sắp chiếu: " + comingSoonFilms.size());
		comingSoonFilms.forEach(f -> System.out.println(" - " + f.getName()));

		List<List<Film>> filmChunks = new ArrayList<>();
		for (int i = 0; i < films.size(); i += 4) {
			int end = Math.min(i + 4, films.size());
			filmChunks.add(films.subList(i, end));
		}

		films.forEach(f -> {
			if (f != null && f.getAvatar() != null) {
				if (!f.getAvatar().startsWith("http") && !f.getAvatar().startsWith("/film-images/")) {
					f.setAvatar("/film-images/" + f.getAvatar());
				}
			}
		});

		model.addAttribute("films", films);
		model.addAttribute("filmChunks", filmChunks);
		model.addAttribute("carouselFilms", carouselFilms);
		model.addAttribute("comingSoonFilms", comingSoonFilms);

		return "home/index";
	}
}

package com.ht_cinema.controller.admin;

import com.ht_cinema.model.Film;
import com.ht_cinema.repository.AccountRepository;
import com.ht_cinema.repository.CinemasRepository;
import com.ht_cinema.repository.FilmRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AdminHomeController {

	@Autowired
	private FilmRepository filmRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private CinemasRepository cinemasRepository;

	@GetMapping("/admin/index")
	public String home(Model model) {

	    long dangChieu = filmRepository.countByStatus(1);
	    long sapChieu = filmRepository.countByStatus(2);
	    long taiKhoan = accountRepository.count();
	    long soRap = cinemasRepository.count();

	    List<Film> top5PhimDangChieu = filmRepository.findTop5DangChieu(PageRequest.of(0, 5));

	    model.addAttribute("dangChieu", dangChieu);
	    model.addAttribute("sapChieu", sapChieu);
	    model.addAttribute("taiKhoan", taiKhoan);
	    model.addAttribute("soRap", soRap);
	    model.addAttribute("top5PhimDangChieu", top5PhimDangChieu);

	    model.addAttribute("activeParent", "thongKe");
	    model.addAttribute("activePage", "index");
	    model.addAttribute("activeSubPage", "overview");

	    return "admin/index";
	}

}

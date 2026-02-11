package com.ht_cinema.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import com.ht_cinema.config.CustomUserDetails;
import com.ht_cinema.model.Account;
import com.ht_cinema.model.Film;
import com.ht_cinema.service.RatingService;
import com.ht_cinema.repository.FilmRepository;

@Controller
@RequestMapping("/rating")
public class RatingController {
	private Account getCurrentAccount() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails user) {
			return user.getAccount();
		}
		return null;
	}
    private final RatingService ratingService;
    private final FilmRepository filmRepository;

    public RatingController(RatingService ratingService, FilmRepository filmRepository) {
        this.ratingService = ratingService;
        this.filmRepository = filmRepository;
    }

    @PostMapping("/add/{filmId}")
    public String addRating(@PathVariable Integer filmId,
                            @RequestParam("star") int star,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
    	Account account = getCurrentAccount();
        if (account == null) {
            return "redirect:/login/form";
        }

        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new IllegalArgumentException("Phim không tồn tại"));

        try {
            ratingService.addOrUpdateRating(account, film, star);
            redirectAttributes.addFlashAttribute("popupMessage", "Đánh giá thành công!");
            redirectAttributes.addFlashAttribute("popupType", "success");

            Integer userRating = ratingService.findUserRating(account.getId(), film.getId());
            redirectAttributes.addFlashAttribute("userRating", userRating);

        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("popupMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("popupType", "error");
        }

        return "redirect:/film/detail/" + filmId;
    }}
package com.ht_cinema.controller;

import com.ht_cinema.config.CustomUserDetails;
import com.ht_cinema.model.Account;
import com.ht_cinema.model.Comment;
import com.ht_cinema.model.Film;
import com.ht_cinema.model.SuatChieu;
import com.ht_cinema.repository.CommentRepository;
import com.ht_cinema.repository.FilmRepository;
import com.ht_cinema.repository.SuatChieuRepository;
import com.ht_cinema.service.CommentService;
import com.ht_cinema.service.RatingService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class DetailFilmController {
	private Account getCurrentAccount() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails user) {
			return user.getAccount();
		}
		return null;
	}
	  @Autowired
	    private CommentService commentService;

	    @Autowired
	    private RatingService ratingService;

	    @Autowired
	    private FilmRepository filmRepository;

	    @Autowired
	    private CommentRepository commentRepository;

	    @Autowired
	    private SuatChieuRepository suatChieuRepository;
	@GetMapping("/detailProduct")
	public String DetailProduct() {
		return "film/productsDetail";
	}
	@GetMapping("/film/detail")
	public String detailFilm(@RequestParam("id") Integer id, Model model) {
		Optional<Film> optionalFilm = filmRepository.findById(id);
		if (optionalFilm.isEmpty()) {
			return "redirect:/";
		}

		Film film = optionalFilm.get();

		String formattedDate = "";
		if (film.getDetailFilm() != null && film.getDetailFilm().getDate() != null) {
			formattedDate = film.getDetailFilm().getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		}
		String trailerUrl = null;
		if (film.getTrailer() != null && !film.getTrailer().isEmpty()) {
			String url = film.getTrailer();
			if (url.contains("watch?v=")) {
				trailerUrl = url.replace("watch?v=", "embed/");
			} else if (url.contains("youtu.be/")) {
				String videoId = url.substring(url.lastIndexOf("/") + 1);
				if (videoId.contains("?"))
					videoId = videoId.substring(0, videoId.indexOf("?"));
				trailerUrl = "https://www.youtube.com/embed/" + videoId;
			} else {
				trailerUrl = url;
			}
		}

		if (film.getAvatar() != null && !film.getAvatar().startsWith("http")) {
			film.setAvatar("/film-images/" + film.getAvatar());
		}
		if (film.getPoster() != null && !film.getPoster().startsWith("http")) {
			film.setPoster("/poster-images/" + film.getPoster());
		}

		List<SuatChieu> showTimes = suatChieuRepository.findByFilmId(film.getId());

		List<String> theLoaiList = new ArrayList<>();
		if (film.getCategoryFilm() != null && !film.getCategoryFilm().isEmpty()) {
			film.getCategoryFilm().forEach(cf -> {
				if (cf.getCategories() != null) {
					theLoaiList.add(cf.getCategories().getName());
				}
			});
		}
        List<Comment> comments = commentService.getComments(film.getId());
        Long commentCount = commentService.getCount(film.getId());

        Double averageStar = ratingService.getAverageStar(film.getId());
        Long ratingCount = ratingService.getCount(film.getId());


        model.addAttribute("film", film);
        model.addAttribute("formattedDate", formattedDate);
        model.addAttribute("trailerUrl", trailerUrl);
        model.addAttribute("showTimes", showTimes);
        model.addAttribute("theLoaiList", theLoaiList);
        model.addAttribute("comments", comments);
        model.addAttribute("commentCount", commentCount);
        model.addAttribute("averageStar", averageStar);
        model.addAttribute("ratingCount", ratingCount);

		return "film/productsDetail";
	}
	@PostMapping("/film/{id}/comment")
    public String addComment(@PathVariable Integer id,
                             @RequestParam String content,
                             HttpSession session,
                             RedirectAttributes ra) {
		Account account = getCurrentAccount();
		if (account == null) {
			return "redirect:/login/form";
		}

        Film film = filmRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Phim không tồn tại"));

        if (commentRepository.existsByAccountAndFilm(account, film)) {
            ra.addFlashAttribute("popupMessage", "Bạn chỉ được bình luận một lần cho phim này!");
            ra.addFlashAttribute("popupType", "error");
            return "redirect:/film/detail?id=" + id;
        }

        Comment comment = new Comment();
        comment.setAccount(account);
        comment.setFilm(film);
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());
        commentRepository.save(comment);

        ra.addFlashAttribute("popupMessage", "Bình luận thành công!");
        ra.addFlashAttribute("popupType", "success");
        return "redirect:/film/detail?id=" + id;
    }

    @PostMapping("/film/{id}/rating")
    public String addRating(@PathVariable Integer id,
                            @RequestParam int star,
                            HttpSession session,
                            RedirectAttributes ra) {
    	Account account = getCurrentAccount();
		if (account == null) {
			return "redirect:/login/form";
		}

        Film film = filmRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Phim không tồn tại"));

        try {
            ratingService.addOrUpdateRating(account, film, star);
            ra.addFlashAttribute("popupMessage", "Đánh giá thành công!");
            ra.addFlashAttribute("popupType", "success");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("popupMessage", e.getMessage());
            ra.addFlashAttribute("popupType", "error");
        }

        return "redirect:/film/detail?id=" + id;
    }
}

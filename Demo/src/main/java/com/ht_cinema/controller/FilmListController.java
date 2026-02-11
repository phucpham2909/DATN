package com.ht_cinema.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ht_cinema.model.Film;
import com.ht_cinema.service.FilmService;

@Controller
public class FilmListController {

    @Autowired
    private FilmService filmService;

    @GetMapping("/film/nowShowing")
    public String nowShowing(Model model) {
        Page<Film> films = filmService.findByStatus(1, PageRequest.of(0, 12));

        // Xử lý đường dẫn ảnh
        films.getContent().forEach(film -> {
            if (film.getAvatar() != null && !film.getAvatar().startsWith("http")) {
                film.setAvatar("/film-images/" + film.getAvatar());
            }
        });

        model.addAttribute("films", films.getContent());
        return "film/nowShowing";
    }

    @GetMapping("/film/comingSoon")
    public String comingSoon(Model model) {
        Page<Film> films = filmService.findByStatus(2, PageRequest.of(0, 12));

        // Xử lý đường dẫn ảnh
        films.getContent().forEach(film -> {
            if (film.getAvatar() != null && !film.getAvatar().startsWith("http")) {
                film.setAvatar("/film-images/" + film.getAvatar());
            }
        });

        model.addAttribute("films", films.getContent());
        return "film/comingSoon";
    }

}

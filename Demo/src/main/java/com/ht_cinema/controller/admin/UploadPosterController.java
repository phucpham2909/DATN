package com.ht_cinema.controller.admin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ht_cinema.model.Film;
import com.ht_cinema.repository.FilmRepository;

@Controller
@RequestMapping("/admin/film")
public class UploadPosterController {

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/poster-images/";

    @Autowired
    private FilmRepository filmRepository;

    @GetMapping("/posters")
    public String showPosters(Model model,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "5") int size,
                              @RequestParam(required = false) String keyword,
                              @RequestParam(required = false) String hasPoster) {

        List<Film> allFilms = filmRepository.findAll();

        if (keyword != null && !keyword.isEmpty()) {
            allFilms = allFilms.stream()
                    .filter(f -> f.getName().toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());
        }

        Boolean posterFilter = null;
        if ("true".equals(hasPoster)) {
            allFilms = allFilms.stream()
                    .filter(f -> f.getPoster() != null && !f.getPoster().isEmpty())
                    .collect(Collectors.toList());
        } else if ("false".equals(hasPoster)) {
            allFilms = allFilms.stream()
                    .filter(f -> f.getPoster() == null || f.getPoster().isEmpty())
                    .collect(Collectors.toList());
        }

        int start = page * size;
        int end = Math.min(start + size, allFilms.size());
        List<Film> pagedFilms = (start > end) ? List.of() : allFilms.subList(start, end);

        model.addAttribute("films", pagedFilms);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", (int) Math.ceil((double) allFilms.size() / size));
        model.addAttribute("keyword", keyword);
        model.addAttribute("hasPoster", hasPoster);
		model.addAttribute("activeParent", "film");
		model.addAttribute("activePage", "poster");

        model.addAttribute("filmsForUpload", filmRepository.findAll());

        return "admin/film/poster";
    }

    @PostMapping("/upload-poster")
    public String uploadPoster(@RequestParam("filmId") Integer filmId,
                               @RequestParam("posterFile") MultipartFile posterFile) {

        Film film = filmRepository.findById(filmId).orElse(null);
        if (film == null) return "redirect:/admin/film/posters";

        if (!posterFile.isEmpty()) {
            try {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

                String fileName = System.currentTimeMillis() + "_" + posterFile.getOriginalFilename();
                Files.copy(posterFile.getInputStream(), uploadPath.resolve(fileName),
                        StandardCopyOption.REPLACE_EXISTING);

                film.setPoster(fileName);
                filmRepository.save(film);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "redirect:/admin/film/posters";
    }

    @PostMapping("/delete-poster")
    public String deletePoster(@RequestParam("filmId") Integer filmId) {
        Film film = filmRepository.findById(filmId).orElse(null);
        if (film != null) {
            film.setPoster(null);
            filmRepository.save(film);
        }
        return "redirect:/admin/film/posters";
    }
}

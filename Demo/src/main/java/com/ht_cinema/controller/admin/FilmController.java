package com.ht_cinema.controller.admin;

import com.ht_cinema.model.CategoriesFilms;
import com.ht_cinema.model.DetailFilms;
import com.ht_cinema.model.Film;
import com.ht_cinema.service.CategoryService;
import com.ht_cinema.service.FilmService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/film")
public class FilmController {

	private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/film-images/";

	@Autowired
	private FilmService filmService;

	@Autowired
	private CategoryService categoryService;

	@GetMapping("/add")
	public String showAddForm(Model model) {
		Film film = new Film();
		film.setDetailFilm(new DetailFilms());
		model.addAttribute("film", film);
		model.addAttribute("allCategories", categoryService.findAll());
		model.addAttribute("activeParent", "film");
		model.addAttribute("activePage", "film");
		return "admin/film/add";
	}

	@PostMapping("/add")
	@Transactional
	public String addFilm(@Valid @ModelAttribute Film film, BindingResult result,
			@RequestParam("photo") MultipartFile photo, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("allCategories", categoryService.findAll());
			return "admin/film/add";
		}

		String fileName = handleFileUpload(photo, null);
		film.setAvatar(fileName);

		if (film.getDetailFilm() != null)
			film.getDetailFilm().setFilm(film);

		List<CategoriesFilms> categoriesFilms = new ArrayList<>();
		if (film.getCategoryIds() != null) {
			film.getCategoryIds().forEach(catId -> categoryService.findById(catId).ifPresent(category -> {
				CategoriesFilms cf = new CategoriesFilms();
				cf.setFilm(film);
				cf.setCategories(category);
				categoriesFilms.add(cf);
			}));
		}
		film.setCategoryFilm(categoriesFilms);

		filmService.save(film);
		return "redirect:/admin/film/list";
	}

	@GetMapping("/edit/{id}")
	public String showEditForm(@PathVariable Integer id, Model model) {
		Optional<Film> optionalFilm = filmService.findById(id);
		if (optionalFilm.isEmpty())
			return "redirect:/admin/film/list";

		Film film = optionalFilm.get();
		if (film.getDetailFilm() == null)
			film.setDetailFilm(new DetailFilms());

		List<Integer> categoryIds = new ArrayList<>();
		if (film.getCategoryFilm() != null) {
			film.getCategoryFilm().forEach(cf -> {
				if (cf.getCategories() != null)
					categoryIds.add(cf.getCategories().getId());
			});
		}
		film.setCategoryIds(categoryIds);

		model.addAttribute("film", film);
		model.addAttribute("allCategories", categoryService.findAll());
		model.addAttribute("activeParent", "film");
		model.addAttribute("activePage", "film");
		return "admin/film/edit";
	}

	@PostMapping("/edit")
	@Transactional
	public String updateFilm(@Valid @ModelAttribute Film film, BindingResult result,
			@RequestParam("photo") MultipartFile photo, Model model) {

		Film existing = filmService.findById(film.getId()).orElse(null);
		if (existing == null)
			return "redirect:/admin/film/list";

		if (result.hasErrors()) {
			model.addAttribute("allCategories", categoryService.findAll());
			return "admin/film/edit";
		}

		String fileName = handleFileUpload(photo, existing.getAvatar());
		if (fileName != null)
			existing.setAvatar(fileName);

		DetailFilms detail = existing.getDetailFilm();
		if (detail == null) {
			detail = new DetailFilms();
			detail.setFilm(existing);
		}
		if (film.getDetailFilm() != null) {
			detail.setDaoDien(film.getDetailFilm().getDaoDien());
			detail.setDienVien(film.getDetailFilm().getDienVien());
			detail.setDate(film.getDetailFilm().getDate());
			detail.setThoiLuong(film.getDetailFilm().getThoiLuong());
			detail.setNgonNgu(film.getDetailFilm().getNgonNgu());
			detail.setRate(film.getDetailFilm().getRate());
			detail.setTheLoai(film.getDetailFilm().getTheLoai());
		}
		existing.setDetailFilm(detail);

		existing.getCategoryFilm().clear();
		if (film.getCategoryIds() != null) {
			film.getCategoryIds().forEach(catId -> categoryService.findById(catId).ifPresent(category -> {
				CategoriesFilms cf = new CategoriesFilms();
				cf.setFilm(existing);
				cf.setCategories(category);
				existing.getCategoryFilm().add(cf);
			}));
		}

		existing.setName(film.getName());
		existing.setStatus(film.getStatus());
		existing.setTrailer(film.getTrailer());
		existing.setType(film.getType());

		filmService.save(existing);
		return "redirect:/admin/film/list";
	}

	@GetMapping("/list")
	public String listFilms(
			Model model,
			@RequestParam(defaultValue = "0") int page, 
			@RequestParam(defaultValue = "5") int size,
			@RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sort", defaultValue = "asc") String sort
			) {
		Sort sortOrder = sort.equalsIgnoreCase("desc") ? Sort.by("name").descending() : Sort.by("name").ascending();
        Pageable pageable = PageRequest.of(page, size, sortOrder);
        
		Page<Film> films = (keyword != null && !keyword.isEmpty()) ? filmService.searchByName(keyword, pageable)
				: filmService.findAll(pageable);

		model.addAttribute("films", films);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", films.getTotalPages());
		model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);
		model.addAttribute("activeParent", "film");
		model.addAttribute("activePage", "film");

		return "admin/film/film_list";
	}

	@GetMapping("/delete/{id}")
	@Transactional
	public String deleteFilmById(@PathVariable("id") Integer id) {
		Optional<Film> optionalFilm = filmService.findById(id);
		if (optionalFilm.isEmpty()) {
			return "redirect:/admin/film/list";
		}
		Film film = optionalFilm.get();

		if (film.getSuatChieus() != null) {
			film.getSuatChieus().forEach(sc -> sc.setFilm(null));
			film.getSuatChieus().clear();
		}

		if (film.getDetailFilm() != null) {
			film.getDetailFilm().setFilm(null);
			film.setDetailFilm(null);
		}

		if (film.getCategoryFilm() != null && !film.getCategoryFilm().isEmpty()) {
			film.getCategoryFilm().forEach(cf -> {
				cf.setFilm(null);
				cf.setCategories(null);
			});
			film.getCategoryFilm().clear();
		}

		filmService.deleteFilmById(id);
		return "redirect:/admin/film/list";
	}

	private String handleFileUpload(MultipartFile photo, String oldFileName) {
		if (photo == null || photo.isEmpty())
			return null;

		try {
			Path uploadPath = Paths.get(UPLOAD_DIR);
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			if (oldFileName != null) {
				Files.deleteIfExists(uploadPath.resolve(oldFileName));
			}

			String fileName = System.currentTimeMillis() + "_" + photo.getOriginalFilename();
			Files.copy(photo.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
			return fileName;
		} catch (Exception e) {
			System.err.println("Lỗi upload file: " + e.getMessage());
			return null;
		}
	}
}

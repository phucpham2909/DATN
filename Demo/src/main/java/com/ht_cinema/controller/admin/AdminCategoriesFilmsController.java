package com.ht_cinema.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ht_cinema.model.CategoriesFilms;
import com.ht_cinema.repository.CategoriesFilmsRepository;
import com.ht_cinema.repository.CategoriesRepository;
import com.ht_cinema.repository.FilmRepository;
import com.ht_cinema.service.CategoryService;

@Controller
@RequestMapping("/admin/categories-films")
public class AdminCategoriesFilmsController {

	@Autowired
	private CategoriesFilmsRepository categoriesFilmsRepository;

	@Autowired
	private CategoriesRepository categoriesRepository;

	@Autowired
	private FilmRepository filmRepository;

	@Autowired
	private CategoryService categoryService;

	@GetMapping("/list")
	public String listCategories(Model model) {
		model.addAttribute("categories", categoryService.findAll(null));
		return "admin/category_list";
	}

	@GetMapping("/add")
	public String addForm(Model model) {
		model.addAttribute("catFilm", new CategoriesFilms());
		model.addAttribute("categories", categoriesRepository.findAll());
		model.addAttribute("films", filmRepository.findAll());
		return "admin/categories_films/add";
	}

	@PostMapping("/add")
	public String add(@ModelAttribute CategoriesFilms catFilm, Model model) {
		categoriesFilmsRepository.save(catFilm);
		model.addAttribute("message", "Thêm liên kết phim - danh mục thành công!");
		model.addAttribute("catFilm", new CategoriesFilms());
		model.addAttribute("categories", categoriesRepository.findAll());
		model.addAttribute("films", filmRepository.findAll());
		return "admin/categories_films/add";
	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable("id") Integer id) {
		categoriesFilmsRepository.deleteById(id);
		return "redirect:/admin/categories-films/list";
	}
}

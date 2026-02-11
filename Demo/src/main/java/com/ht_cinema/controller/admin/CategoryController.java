package com.ht_cinema.controller.admin;

import com.ht_cinema.model.Category;
import com.ht_cinema.service.CategoryService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {

	@Autowired
	private CategoryService categoryService;

	@GetMapping
	public String listCategories(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "keyword", defaultValue = "") String keyword,
			@RequestParam(value = "sort", defaultValue = "asc") String sort) {
		Sort sortOrder = sort.equalsIgnoreCase("desc") ? Sort.by("name").descending() : Sort.by("name").ascending();
		Pageable pageable = PageRequest.of(page, size, sortOrder);

		Page<Category> categories;
		if (keyword.isEmpty()) {
			categories = categoryService.findAll(pageable);
		} else {
			categories = categoryService.findByNameContaining(keyword, pageable);
		}

		model.addAttribute("list", categories.getContent());
		model.addAttribute("categories", categories);
		model.addAttribute("category", new Category());
		model.addAttribute("keyword", keyword);
		model.addAttribute("sort", sort);
		model.addAttribute("activeParent", "film");
		model.addAttribute("activePage", "film-category");

		return "admin/category/list";
	}

	@PostMapping("/add")
	public String addCategory(@Valid @ModelAttribute("category") Category category, BindingResult result,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "keyword", defaultValue = "") String keyword,
			@RequestParam(value = "sort", defaultValue = "asc") String sort, Model model) {

		Sort sortOrder = sort.equalsIgnoreCase("desc") ? Sort.by("name").descending() : Sort.by("name").ascending();

		Pageable pageable = PageRequest.of(page, size, sortOrder);

		Page<Category> catPage = keyword.isEmpty() ? categoryService.findAll(pageable)
				: categoryService.findByNameContaining(keyword, pageable);

		if (result.hasErrors()) {
			model.addAttribute("list", catPage.getContent());
			model.addAttribute("categories", catPage);
			model.addAttribute("currentPage", page);
			model.addAttribute("keyword", keyword);
			model.addAttribute("sort", sort);
			model.addAttribute("pageSize", size);
			model.addAttribute("activeParent", "film");
			model.addAttribute("activePage", "film-category");

			return "admin/category/list";
		}

		try {
			categoryService.save(category);
		} catch (IllegalArgumentException e) {
			result.rejectValue("name", "error.category", e.getMessage());

			model.addAttribute("list", catPage.getContent());
			model.addAttribute("categories", catPage);
			model.addAttribute("currentPage", page);
			model.addAttribute("keyword", keyword);
			model.addAttribute("sort", sort);
			model.addAttribute("pageSize", size);

			return "admin/category/list";
		}

		return "redirect:/admin/categories";
	}

	@PostMapping("/edit")
	public String editCategory(@Valid @ModelAttribute("category") Category category, BindingResult result,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "keyword", defaultValue = "") String keyword,
			@RequestParam(value = "sort", defaultValue = "asc") String sort, Model model) {

		Sort sortOrder = sort.equalsIgnoreCase("desc") ? Sort.by("name").descending() : Sort.by("name").ascending();

		Pageable pageable = PageRequest.of(page, size, sortOrder);

		Page<Category> catPage = keyword.isEmpty() ? categoryService.findAll(pageable)
				: categoryService.findByNameContaining(keyword, pageable);

		if (result.hasErrors()) {
			model.addAttribute("list", catPage.getContent());
			model.addAttribute("categories", catPage);
			model.addAttribute("currentPage", page);
			model.addAttribute("keyword", keyword);
			model.addAttribute("sort", sort);
			model.addAttribute("pageSize", size);

			return "admin/category/list";
		}

		try {
			categoryService.save(category);
		} catch (IllegalArgumentException e) {
			result.rejectValue("name", "error.category", e.getMessage());

			model.addAttribute("list", catPage.getContent());
			model.addAttribute("categories", catPage);
			model.addAttribute("currentPage", page);
			model.addAttribute("keyword", keyword);
			model.addAttribute("sort", sort);
			model.addAttribute("pageSize", size);

			return "admin/category/list";
		}

		return "redirect:/admin/categories";
	}

	@GetMapping("/delete/{id}")
	public String deleteCategory(@PathVariable("id") Integer id) {
		categoryService.delete(id);
		return "redirect:/admin/categories";
	}

	@GetMapping("/edit/{id}")
	public String editForm(@PathVariable("id") Integer id, @RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "keyword", defaultValue = "") String keyword,
			@RequestParam(value = "sort", defaultValue = "asc") String sort, Model model) {

		Category category = categoryService.findById(id).orElse(new Category());

		Sort sortOrder = sort.equalsIgnoreCase("desc") ? Sort.by("name").descending() : Sort.by("name").ascending();
		Pageable pageable = PageRequest.of(page, size, sortOrder);

		Page<Category> categories;
		if (keyword.isEmpty()) {
			categories = categoryService.findAll(pageable);
		} else {
			categories = categoryService.findByNameContaining(keyword, pageable);
		}

		model.addAttribute("list", categories.getContent());
		model.addAttribute("categories", categories);
		model.addAttribute("category", category);
		model.addAttribute("keyword", keyword);
		model.addAttribute("sort", sort);
		model.addAttribute("activeParent", "film");
		model.addAttribute("activePage", "film-category");

		return "admin/category/list";
	}
}

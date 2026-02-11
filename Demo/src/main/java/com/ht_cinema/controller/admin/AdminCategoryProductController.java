package com.ht_cinema.controller.admin;

import com.ht_cinema.model.Category;
import com.ht_cinema.model.CategoryProduct;
import com.ht_cinema.service.CategoryProductService;
import com.ht_cinema.service.CategoryService;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/category-product")
public class AdminCategoryProductController {
	@Autowired
	private CategoryProductService categoryProductService;

	@GetMapping
	public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "keyword", defaultValue = "") String keyword,
			@RequestParam(value = "sort", defaultValue = "asc") String sort) {

		Sort sortOrder = sort.equalsIgnoreCase("desc") ? Sort.by("name").descending() : Sort.by("name").ascending();
		Pageable pageable = PageRequest.of(page, size, sortOrder);

		Page<CategoryProduct> catPage;
		if (keyword.isEmpty()) {
			catPage = categoryProductService.findAll(pageable);
		} else {
			catPage = categoryProductService.findByNameContaining(keyword, pageable);
		}
		model.addAttribute("category", new CategoryProduct());
		model.addAttribute("list", catPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("categories", catPage);
		model.addAttribute("totalPage", catPage.getTotalPages());
		model.addAttribute("pageLabels", generatePageLabels(catPage.getTotalPages()));
		model.addAttribute("keyword", keyword);
		model.addAttribute("sort", sort);
		model.addAttribute("pageSize", size);
		
        model.addAttribute("activeParent", "product");
        model.addAttribute("activePage", "cat-product");
		return "admin/categoryProduct/list";
	}

	private List<Integer> generatePageLabels(int totalPage) {
		List<Integer> labels = new ArrayList<>();
		for (int i = 1; i <= totalPage; i++) {
			labels.add(i);
		}
		return labels;
	}

	@PostMapping("/add")
	public String addCategory(
	        @Valid @ModelAttribute("category") CategoryProduct categoryProduct,
	        BindingResult result,
	        @RequestParam(value = "page", defaultValue = "0") int page,
	        @RequestParam(value = "size", defaultValue = "10") int size,
	        @RequestParam(value = "keyword", defaultValue = "") String keyword,
	        @RequestParam(value = "sort", defaultValue = "asc") String sort,
	        Model model) {

	    Sort sortOrder = sort.equalsIgnoreCase("desc")
	            ? Sort.by("name").descending()
	            : Sort.by("name").ascending();

	    Pageable pageable = PageRequest.of(page, size, sortOrder);

	    Page<CategoryProduct> catPage = keyword.isEmpty()
	            ? categoryProductService.findAll(pageable)
	            : categoryProductService.findByNameContaining(keyword, pageable);

	    if (result.hasErrors()) {
	        model.addAttribute("list", catPage.getContent());
	        model.addAttribute("categories", catPage);
	        model.addAttribute("currentPage", page);
	        model.addAttribute("keyword", keyword);
	        model.addAttribute("sort", sort);
	        model.addAttribute("pageSize", size);

	        return "admin/categoryProduct/list"; 
	    }

	    try {
	        categoryProductService.save(categoryProduct);
	    } catch (IllegalArgumentException e) {
	        result.rejectValue("name", "error.categoryProduct", e.getMessage());

	        model.addAttribute("list", catPage.getContent());
	        model.addAttribute("categories", catPage);
	        model.addAttribute("currentPage", page);
	        model.addAttribute("keyword", keyword);
	        model.addAttribute("sort", sort);
	        model.addAttribute("pageSize", size);

	        return "admin/categoryProduct/list"; 
	    }

	    return "redirect:/admin/category-product";
	}

	@PostMapping("/edit")
	public String editCategory(@Valid @ModelAttribute("category") CategoryProduct categoryProduct,
	                           BindingResult result,
	                           @RequestParam(value = "page", defaultValue = "0") int page,
	                           @RequestParam(value = "size", defaultValue = "10") int size,
	                           @RequestParam(value = "keyword", defaultValue = "") String keyword,
	                           @RequestParam(value = "sort", defaultValue = "asc") String sort,
	                           Model model) {
	    if (result.hasErrors()) {
	        Sort sortOrder = sort.equalsIgnoreCase("desc") ? Sort.by("name").descending() : Sort.by("name").ascending();
	        Pageable pageable = PageRequest.of(page, size, sortOrder);
	        Page<CategoryProduct> catPage = keyword.isEmpty()
	                ? categoryProductService.findAll(pageable)
	                : categoryProductService.findByNameContaining(keyword, pageable);

	        model.addAttribute("list", catPage.getContent());
	        model.addAttribute("categories", catPage);
	        model.addAttribute("keyword", keyword);
	        model.addAttribute("sort", sort);
	        model.addAttribute("pageSize", size);
	        return "admin/categoryProduct/list";
	    }

	    try {
	        categoryProductService.save(categoryProduct);
	    } catch (IllegalArgumentException e) {
	        result.rejectValue("name", "error.categoryProduct", e.getMessage());

	        Sort sortOrder = sort.equalsIgnoreCase("desc") ? Sort.by("name").descending() : Sort.by("name").ascending();
	        Pageable pageable = PageRequest.of(page, size, sortOrder);
	        Page<CategoryProduct> catPage = keyword.isEmpty()
	                ? categoryProductService.findAll(pageable)
	                : categoryProductService.findByNameContaining(keyword, pageable);

	        model.addAttribute("list", catPage.getContent());
	        model.addAttribute("categories", catPage);
	        model.addAttribute("keyword", keyword);
	        model.addAttribute("sort", sort);
	        model.addAttribute("pageSize", size);
	        return "admin/categoryProduct/list";
	    }

	    return "redirect:/admin/category-product";
	}
	@GetMapping("/delete/{id}")
	public String delete(@PathVariable("id") Integer id, RedirectAttributes ra) {
	    try {
	        categoryProductService.delete(id);
	        ra.addFlashAttribute("success", "Xóa danh mục thành công");
	    } catch (IllegalArgumentException e) {
	        ra.addFlashAttribute("error", e.getMessage());
	    }
	    return "redirect:/admin/category-product";
	}

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Integer id,
                           @RequestParam(value = "page", defaultValue = "0") int page,
                           @RequestParam(value = "size", defaultValue = "10") int size,
                           @RequestParam(value = "keyword", defaultValue = "") String keyword,
                           @RequestParam(value = "sort", defaultValue = "asc") String sort,
                           Model model) {

    	CategoryProduct category = categoryProductService.findById(id).orElse(new CategoryProduct());

        Sort sortOrder = sort.equalsIgnoreCase("desc") ? Sort.by("name").descending() : Sort.by("name").ascending();
        Pageable pageable = PageRequest.of(page, size, sortOrder);

        Page<CategoryProduct> categories;
        if (keyword.isEmpty()) {
            categories = categoryProductService.findAll(pageable);
        } else {
            categories = categoryProductService.findByNameContaining(keyword, pageable);
        }

        model.addAttribute("list", categories.getContent());
        model.addAttribute("categories", categories);
        model.addAttribute("category", category);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);
        model.addAttribute("activeParent", "film");
        model.addAttribute("activePage", "film-category");

        return "admin/categoryProduct/list";
    }
}

package com.ht_cinema.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ht_cinema.model.Product;
import com.ht_cinema.service.CategoryProductService;
import com.ht_cinema.service.ProductService;
import com.ht_cinema.service.StorageService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/product")
public class AdminProductController {
	@Autowired
	ProductService productService;
	@Autowired
	CategoryProductService categoryProductService;
	@Autowired
	StorageService storageService;

	@GetMapping("/form")
	public String showProductForm(@RequestParam(required = false) Integer id, Model model) {
		Product product = (id != null) ? productService.getProductById(id) : new Product();
		model.addAttribute("product", product);
		model.addAttribute("categories", categoryProductService.findAll());

		return "admin/product/product-form";
	}

	@PostMapping("/save")
	public String saveProduct(@Valid @ModelAttribute Product product, BindingResult result,
			@RequestParam("imageFile") MultipartFile imageFile, @RequestParam("oldImage") String oldImage,
			Model model) {

		model.addAttribute("categories", categoryProductService.findAll());

		if (result.hasErrors()) {
			return "admin/product/product-form";
		}

		if (imageFile.isEmpty()) {
			product.setImages(oldImage);
		} else {
			String fileName = storageService.save(imageFile);
			product.setImages(fileName);
		}

		try {
			productService.save(product);
		} catch (IllegalArgumentException e) {
			result.rejectValue("name", "error.product", e.getMessage());
			return "admin/product/product-form";
		}

		return "redirect:/admin/product/list";
	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable("id") Integer id, RedirectAttributes ra) {
		try {
			productService.delete(id);
			ra.addFlashAttribute("success", "Xóa sản phẩm thành công");
		} catch (IllegalArgumentException e) {
			ra.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/admin/product/list";
	}

	@GetMapping("/list")
	public String listProducts(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "5") int size,
			@RequestParam(value = "keyword", defaultValue = "") String keyword,
			@RequestParam(value = "sort", defaultValue = "asc") String sort) {

		Sort sortOrder = sort.equalsIgnoreCase("desc") ? Sort.by("name").descending() : Sort.by("name").ascending();
		Pageable pageable = PageRequest.of(page, size, sortOrder);
		Page<Product> proPage;

		if (keyword.isEmpty()) {
			proPage = productService.findAll(pageable);
		} else {
			proPage = productService.findByNameContaining(keyword, pageable);
		}

		proPage.getContent().forEach(product -> {
			if (product.getImages() != null && !product.getImages().startsWith("http")) {
				product.setImages("/product-images/" + product.getImages());
			}
		});

		model.addAttribute("list", proPage.getContent());
		model.addAttribute("products", proPage);
		model.addAttribute("product", new Product());
		model.addAttribute("keyword", keyword);
		model.addAttribute("sort", sort);

		model.addAttribute("activeParent", "product");
		model.addAttribute("activePage", "products");
		return "admin/product/product-list";
	}

}

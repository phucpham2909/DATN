package com.ht_cinema.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ht_cinema.model.Product;
import com.ht_cinema.service.ProductService;
@Controller
public class ProductController {
	@Autowired
	ProductService productService;
	@GetMapping("/products")
	public String showProduct(Model model) {
        List<Product> products = productService.getAll();
        model.addAttribute("products", products);
		return "client/booking/product";
	}
}

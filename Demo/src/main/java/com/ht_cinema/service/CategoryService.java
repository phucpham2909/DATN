package com.ht_cinema.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ht_cinema.model.Category;
import com.ht_cinema.repository.CategoriesRepository;

@Service
public class CategoryService {

	@Autowired
	private CategoriesRepository categoryRepository;

	public Page<Category> findAll(Pageable pageable) {
		return categoryRepository.findAll(pageable);
	}

	public List<Category> findAll() {
		return categoryRepository.findAll();
	}

	public Page<Category> findByNameContaining(String keyword, Pageable pageable) {
		return categoryRepository.findByNameContainingIgnoreCase(keyword, pageable);
	}

	public Optional<Category> findById(Integer id) {
		return categoryRepository.findById(id);
	}

	public Category save(Category category) {
	    Optional<Category> existing = categoryRepository.findByName(category.getName());

	    if (existing.isPresent()) {
	        if (category.getId() == null || !existing.get().getId().equals(category.getId())) {
	            throw new IllegalArgumentException("Tên danh mục đã tồn tại");
	        }
	    }
		return categoryRepository.save(category);
	}

	public void delete(Integer id) {
		categoryRepository.deleteById(id);
	}
}


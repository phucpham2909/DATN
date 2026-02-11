package com.ht_cinema.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ht_cinema.model.Category;
import com.ht_cinema.model.CategoryProduct;
import com.ht_cinema.repository.CategoriesRepository;
import com.ht_cinema.repository.CategoryProductRepository;
import com.ht_cinema.repository.ProductRepository;

@Service
public class CategoryProductService {

	@Autowired
	private CategoryProductRepository categoryProductRepository;
	@Autowired
	private ProductRepository productRepository;
	
	public Page<CategoryProduct> findAll(Pageable pageable) {
		return categoryProductRepository.findAll(pageable);
	}

	public List<CategoryProduct> findAll() {
		return categoryProductRepository.findAll();
	}

	public Page<CategoryProduct> findByNameContaining(String keyword, Pageable pageable) {
		return categoryProductRepository.findByNameContainingIgnoreCase(keyword, pageable);
	}

	public Optional<CategoryProduct> findById(Integer id) {
		return categoryProductRepository.findById(id);
	}


	public void save(CategoryProduct categoryProduct) {

	    Optional<CategoryProduct> existing = categoryProductRepository.findByName(categoryProduct.getName());

	    if (existing.isPresent()) {
	        // Nếu đang thêm mới (id == null) hoặc id khác với bản ghi tìm thấy → trùng tên
	        if (categoryProduct.getId() == null || !existing.get().getId().equals(categoryProduct.getId())) {
	            throw new IllegalArgumentException("Tên danh mục đã tồn tại");
	        }
	    }

	    categoryProductRepository.save(categoryProduct);
	}



		public void delete(Integer id) {

		    // Nếu danh mục đang có sản phẩm → không cho xóa
		    if (productRepository.existsByCategoryProduct_Id(id)) {
		        throw new IllegalArgumentException("Danh mục đang được sử dụng bởi sản phẩm, không thể xóa");
		    }

		    categoryProductRepository.deleteById(id);
		}
	
}


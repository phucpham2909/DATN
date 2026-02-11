package com.ht_cinema.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ht_cinema.model.Product;
import com.ht_cinema.repository.BookingProductRepository;
import com.ht_cinema.repository.ProductRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


import java.util.List;

@Service
public class ProductService {
	@Autowired
    private BookingProductRepository bookingProductRepo;


    @Autowired
    private ProductRepository productRepository;



    public long countProducts() {
        return productRepository.count();
    }

	public Page<Product> findAll(Pageable pageable) {
		return productRepository.findAll(pageable);
	}

	public List<Product> findAll() {
		return productRepository.findAll();
	}

	public Page<Product> findByNameContaining(String keyword, Pageable pageable) {
		return productRepository.findByNameContainingIgnoreCase(keyword, pageable);
	}
    public Product getProductById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }



    public List<Product> getAll() {
        return productRepository.findAll();
    }
    public void save(Product product) {

        if (product.getId() == null) {
            if (productRepository.existsByName(product.getName())) {
                throw new IllegalArgumentException("Tên sản phẩm đã tồn tại");
            }
        } 
        else {
            Product existing = productRepository.findById(product.getId()).orElse(null);
            if (existing != null && !existing.getName().equals(product.getName())) {
                if (productRepository.existsByName(product.getName())) {
                    throw new IllegalArgumentException("Tên sản phẩm đã tồn tại");
                }
            }
        }

        productRepository.save(product);
    }


    public void delete(Integer productId) {

        if (bookingProductRepo.existsByProductId(productId)) {
            throw new IllegalArgumentException("Sản phẩm đang được sử dụng trong hóa đơn, không thể xóa");
        }

        productRepository.deleteById(productId);
    }

}
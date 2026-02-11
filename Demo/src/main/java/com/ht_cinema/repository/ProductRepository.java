package com.ht_cinema.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ht_cinema.model.Category;
import com.ht_cinema.model.Product;


public interface ProductRepository extends JpaRepository<Product, Integer> {
	List<Product> findByNameContainingIgnoreCase(String keyword);
	Optional<Product> findById(Integer id);
	   boolean existsByName(String name);
	   Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

	    Page<Product> findAllByOrderByNameAsc(Pageable pageable);
	    Page<Product> findAllByOrderByNameDesc(Pageable pageable);
	    boolean existsByCategoryProduct_Id(Integer categoryId);
		List<Product> findAll();

}
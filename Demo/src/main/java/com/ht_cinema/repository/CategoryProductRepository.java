package com.ht_cinema.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ht_cinema.model.Category;
import com.ht_cinema.model.CategoryProduct;

public interface CategoryProductRepository extends JpaRepository<CategoryProduct, Integer>{
	Page<CategoryProduct> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<CategoryProduct> findAllByOrderByNameAsc(Pageable pageable);
    Page<CategoryProduct> findAllByOrderByNameDesc(Pageable pageable);
    boolean existsByName(String name);
    Optional<CategoryProduct> findByName(String name);
	List<CategoryProduct> findAll();
}

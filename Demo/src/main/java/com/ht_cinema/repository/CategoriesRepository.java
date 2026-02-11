package com.ht_cinema.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ht_cinema.model.Category;

public interface CategoriesRepository extends JpaRepository<Category, Integer>{
	Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Category> findAllByOrderByNameAsc(Pageable pageable);
    Page<Category> findAllByOrderByNameDesc(Pageable pageable);

	List<Category> findAll();
	Optional<Category> findByName(String name);
	boolean existsByName(String name);
}

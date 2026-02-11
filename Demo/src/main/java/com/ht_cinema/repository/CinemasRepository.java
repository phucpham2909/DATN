package com.ht_cinema.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ht_cinema.model.Cinemas;

@Repository
public interface CinemasRepository extends JpaRepository<Cinemas, Integer> {
	Page<Cinemas> findByNameContainingIgnoreCase(String name, Pageable pageable);

	Page<Cinemas> findAllByOrderByNameAsc(Pageable pageable);

	Page<Cinemas> findAllByOrderByNameDesc(Pageable pageable);
	List<Cinemas> findAll();

	@Query("SELECT c FROM Cinemas c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) "
			+ "OR LOWER(c.dress) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	Page<Cinemas> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
	boolean existsByNameAndIdNot(String name, Integer id);

	boolean existsByName(String name);
}

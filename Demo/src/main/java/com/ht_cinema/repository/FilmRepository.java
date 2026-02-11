package com.ht_cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.ht_cinema.model.Film;

public interface FilmRepository extends JpaRepository<Film, Integer> {
	Page<Film> findByNameContainingIgnoreCase(String name, Pageable pageable);

	List<Film> findTop12ByOrderByIdDesc();

	List<Film> findTop12ByStatusOrderByIdDesc(int status);

	List<Film> findTop4ByStatusOrderByIdDesc(int status);

	List<Film> findByStatus(Integer status);

	List<Film> findTop4ByOrderByIdDesc();

	List<Film> findTop3ByStatusOrderByDoanhThuDesc(int status);

	long countByStatus(Integer status);

	@Query("SELECT f FROM Film f JOIN f.detailFilm d WHERE f.status = 1 ORDER BY d.date DESC")
	List<Film> findTop5DangChieu(Pageable pageable);

	@Query("SELECT f FROM Film f WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	List<Film> findByKeyword(@Param("keyword") String keyword);

	List<Film> findByStatus(int status);

	Page<Film> findByStatus(Integer status, Pageable pageable);
}

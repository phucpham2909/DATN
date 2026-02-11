package com.ht_cinema.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ht_cinema.model.CategoriesFilms;

import jakarta.transaction.Transactional;

public interface CategoriesFilmsRepository extends JpaRepository<CategoriesFilms, Integer> {
	@Query("SELECT cf FROM CategoriesFilms cf WHERE cf.film.id = :filmId")
	List<CategoriesFilms> findByFilmId(Integer filmId);

	@Transactional
	@Modifying
	@Query("DELETE FROM CategoriesFilms cf WHERE cf.film.id = :filmId")
	void deleteByFilmId(@Param("filmId") Integer filmId);
}

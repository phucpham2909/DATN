package com.ht_cinema.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ht_cinema.model.DetailFilms;

public interface DetailFilmsRepository extends JpaRepository<DetailFilms, Integer> {
	@Query("SELECT df FROM DetailFilms df JOIN FETCH df.film")
    List<DetailFilms> findAllWithFilm();
}

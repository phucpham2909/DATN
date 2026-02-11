package com.ht_cinema.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ht_cinema.model.SuatChieu;

@Repository
public interface SuatChieuRepository extends JpaRepository<SuatChieu, Integer> {
	Page<SuatChieu> findByFilm_NameContainingIgnoreCase(String name, Pageable pageable);

	List<SuatChieu> findByDetailFilm_Film_Id(Integer filmId);

	List<SuatChieu> findByFilmId(Integer filmId);

	Page<SuatChieu> findByDetailFilm_Film_NameContainingIgnoreCaseOrderByDateAscGioBatDauAsc(String filmName,
			Pageable pageable);

	Page<SuatChieu> findByDetailFilm_Film_NameContainingIgnoreCaseAndDateBetweenOrderByDateAscGioBatDauAsc(
			String filmName, LocalDate from, LocalDate to, Pageable pageable);

	Page<SuatChieu> findByDateBetweenOrderByDateAscGioBatDauAsc(LocalDate from, LocalDate to, Pageable pageable);

	Page<SuatChieu> findAllByOrderByDateAscGioBatDauAsc(Pageable pageable);
}

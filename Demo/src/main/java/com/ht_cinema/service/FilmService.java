package com.ht_cinema.service;

import com.ht_cinema.model.Film;
import com.ht_cinema.repository.FilmRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FilmService {

    @Autowired
    private FilmRepository filmRepository;

    public Film save(Film film) {
        return filmRepository.save(film);
    }

    public Page<Film> findAll(Pageable pageable) {
        return filmRepository.findAll(pageable);
    }

    public Page<Film> searchByName(String keyword, Pageable pageable) {
        return filmRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }

    public Optional<Film> findById(Integer id) {
        return filmRepository.findById(id);
    }

    @Transactional
    public void deleteFilmById(Integer id) {
        filmRepository.findById(id).ifPresent(film -> {

            if (film.getDetailFilm() != null) {
                film.setDetailFilm(null);
            }

            if (film.getCategoryFilm() != null && !film.getCategoryFilm().isEmpty()) {
                film.getCategoryFilm().clear();
            }

            if (film.getSuatChieus() != null && !film.getSuatChieus().isEmpty()) {
                film.getSuatChieus().clear();
            }

            filmRepository.delete(film);
        });
    }

    public List<Film> findTop12Newest() {
        return filmRepository.findTop12ByOrderByIdDesc();
    }

    public List<Film> findTop4Newest() {
        return filmRepository.findTop4ByOrderByIdDesc();
    }

    public Page<Film> findByStatus(Integer status, Pageable pageable) {
        return filmRepository.findByStatus(status, pageable);
    }

	public void delete(Film film) {
		// TODO Auto-generated method stub
		
	}
}

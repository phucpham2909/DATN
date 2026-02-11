package com.ht_cinema.service;

import com.ht_cinema.model.DetailFilms;
import com.ht_cinema.repository.DetailFilmsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetailFilmService {

    @Autowired
    private DetailFilmsRepository detailFilmsRepository;

    public DetailFilms save(DetailFilms detailFilms) {
        return detailFilmsRepository.save(detailFilms);
    }

    public List<DetailFilms> findAllWithFilm() {
        return detailFilmsRepository.findAllWithFilm();  
    }

    public DetailFilms findById(Integer id) {
        return detailFilmsRepository.findById(id).orElse(null);
    }

    public void delete(Integer id) {
        detailFilmsRepository.deleteById(id);
    }
}

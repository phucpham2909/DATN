package com.ht_cinema.service;

import com.ht_cinema.model.Cinemas;
import com.ht_cinema.repository.CinemasRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CinemaService {

    @Autowired
    private CinemasRepository cinemasRepository;

    public Cinemas save(Cinemas cinemas) {
        return cinemasRepository.save(cinemas);
    }

    public Page<Cinemas> findAll(Pageable pageable) {
        return cinemasRepository.findAll(pageable);
    }

    public Page<Cinemas> searchByName(String keyword, Pageable pageable) {
        return cinemasRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }
    
    public Page<Cinemas> findByNameContaining(String keyword, Pageable pageable) {
		return cinemasRepository.findByNameContainingIgnoreCase(keyword, pageable);
	}

    public Optional<Cinemas> findById(Integer id) {
        return cinemasRepository.findById(id);
    }

	public void delete(Cinemas cinemas) {
		// TODO Auto-generated method stub
		
	}
}

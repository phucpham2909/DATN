package com.ht_cinema.service;

import com.ht_cinema.model.Rooms;
import com.ht_cinema.repository.RoomsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoomService {

    @Autowired
    private RoomsRepository roomsRepository;

    public Rooms save(Rooms rooms) {
        return roomsRepository.save(rooms);
    }

    public Page<Rooms> findAll(Pageable pageable) {
        return roomsRepository.findAll(pageable);
    }

    public Page<Rooms> searchByName(String keyword, Pageable pageable) {
        return roomsRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }
    
    public Page<Rooms> findByNameContaining(String keyword, Pageable pageable) {
		return roomsRepository.findByNameContainingIgnoreCase(keyword, pageable);
	}

    public Optional<Rooms> findById(Integer id) {
        return roomsRepository.findById(id);
    }

	public void delete(Rooms cinemas) {
		// TODO Auto-generated method stub
		
	}
}

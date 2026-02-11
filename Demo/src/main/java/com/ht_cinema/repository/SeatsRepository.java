package com.ht_cinema.repository;

import com.ht_cinema.model.Rooms;
import com.ht_cinema.model.Seats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatsRepository extends JpaRepository<Seats, Integer> {
    List<Seats> findByRoomOrderById(Rooms room);
    List<Seats> findByRoomId(Integer roomId);
    public List<Seats> findByRoomAndStatus(Rooms room, int status);
    
}

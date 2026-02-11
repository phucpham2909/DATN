package com.ht_cinema.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ht_cinema.model.Rooms;

@Repository
public interface RoomsRepository extends JpaRepository<Rooms, Integer> {
	Page<Rooms> findByNameContainingIgnoreCase(String name, Pageable pageable);

	Page<Rooms> findAllByOrderByNameAsc(Pageable pageable);

	Page<Rooms> findAllByOrderByNameDesc(Pageable pageable);

	@Query("SELECT r FROM Rooms r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) "
			+ "OR LOWER(r.cinema.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	Page<Rooms> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

	List<Rooms> findByCinemaId(Integer cinemaId);
	boolean existsByNameAndCinemaId(String name, Integer cinemaId);

	boolean existsByNameAndCinemaIdAndIdNot(String name, Integer cinemaId, Integer id);
}

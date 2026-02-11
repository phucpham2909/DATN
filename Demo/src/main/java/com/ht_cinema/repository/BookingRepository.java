package com.ht_cinema.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ht_cinema.model.Booking;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

	Page<Booking> findByDateBetween(LocalDate start, LocalDate end, Pageable pageable);

	List<Booking> findByDateBetween(LocalDate start, LocalDate end);

	@Query("SELECT b FROM Booking b WHERE MONTH(b.date) = :month AND YEAR(b.date) = :year")
	Page<Booking> findByMonthAndYear(@Param("month") int month, @Param("year") int year, Pageable pageable);

	@Query("SELECT b FROM Booking b WHERE b.account.id = :accountId AND b.trangThai = true ORDER BY b.id DESC")
	List<Booking> findSuccessfulBookingsByUser(@Param("accountId") Integer accountId);

	@Query("""
			    SELECT DISTINCT b FROM Booking b
			    LEFT JOIN FETCH b.suatChieu sc
			    LEFT JOIN FETCH sc.film f
			    LEFT JOIN FETCH sc.room r
			    LEFT JOIN FETCH r.cinema c
			    LEFT JOIN FETCH b.account a
			""")
	List<Booking> findAllWithDetails();
	@Query("SELECT COUNT(b) FROM Booking b WHERE b.account.id = :accountId AND b.suatChieu.film.id = :filmId AND b.trangThai = true")
    long countBooking(@Param("accountId") Integer accountId,
                      @Param("filmId") Integer filmId);
}

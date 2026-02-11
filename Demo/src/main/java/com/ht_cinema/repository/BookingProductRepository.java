package com.ht_cinema.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ht_cinema.model.BookingProduct;

public interface BookingProductRepository extends JpaRepository<BookingProduct, Integer> {
    @Query("SELECT bp FROM BookingProduct bp WHERE MONTH(bp.booking.date) = :month AND YEAR(bp.booking.date) = :year")
    Page<BookingProduct> findByMonthAndYear(@Param("month") int month, @Param("year") int year, Pageable pageable);

    @Query("SELECT bp FROM BookingProduct bp WHERE bp.booking.date BETWEEN :start AND :end")
    Page<BookingProduct> findByDateBetween(@Param("start") LocalDate start, @Param("end") LocalDate end, Pageable pageable);
    
    boolean existsByProductId(Integer productId);

    @Query("SELECT bp FROM BookingProduct bp WHERE MONTH(bp.booking.date) = :month AND YEAR(bp.booking.date) = :year")
    List<BookingProduct> findByMonthAndYear(@Param("month") int month, @Param("year") int year);

    @Query("SELECT bp FROM BookingProduct bp WHERE bp.booking.date BETWEEN :start AND :end")
    List<BookingProduct> findByDateBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);
    Page<BookingProduct> findByProductNameContaining(String keyword, Pageable pageable);
}
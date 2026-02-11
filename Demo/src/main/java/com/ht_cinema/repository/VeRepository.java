package com.ht_cinema.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ht_cinema.model.Ve;

@Repository
public interface VeRepository extends JpaRepository<Ve, Integer> {

    List<Ve> findByBooking_DateBetween(Date start, Date end);

    List<Ve> findByBookingId(Integer bookingId);

    
    @Query("""
        SELECT v FROM Ve v
        WHERE v.booking.suatChieu.id = :suatChieuId
        AND v.booking.trangThai = true
    """)
    List<Ve> findBookedSeats(@Param("suatChieuId") Integer suatChieuId);
}

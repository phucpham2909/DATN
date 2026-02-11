package com.ht_cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

import com.ht_cinema.model.BookingProduct;

public interface BookingComboRepository
        extends JpaRepository<BookingProduct, Integer> {

    List<BookingProduct> findByBooking_Id(Integer bookingId);

    Optional<BookingProduct>
    findByBooking_IdAndProduct_Id(
            Integer bookingId,
            Integer productId
    );
}

package com.ht_cinema.repository;

import org.springframework.stereotype.Repository;

import com.ht_cinema.model.Account;
import com.ht_cinema.model.Film;
import com.ht_cinema.model.Rating;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer> {
    // Lấy tất cả rating theo filmId
    List<Rating> findByFilmId(Integer filmId);

    // Tìm rating của một account cho một film
    Optional<Rating> findByAccountAndFilm(Account account, Film film);

    // Tính trung bình số sao
    @Query("SELECT AVG(r.star) FROM Rating r WHERE r.film.id = :filmId")
    Double getAverageStar(@Param("filmId") Integer filmId);

    // Đếm số lượng rating
    @Query("SELECT COUNT(r) FROM Rating r WHERE r.film.id = :filmId")
    Long getCount(@Param("filmId") Integer filmId);
    
    
}

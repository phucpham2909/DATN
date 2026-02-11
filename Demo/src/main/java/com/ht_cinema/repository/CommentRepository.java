package com.ht_cinema.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.ht_cinema.model.Account;
import com.ht_cinema.model.Comment;
import com.ht_cinema.model.Film;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    // Lấy danh sách comment theo filmId, sắp xếp mới nhất trước
    List<Comment> findByFilmIdOrderByCreatedAtDesc(Integer filmId);

    boolean existsByAccountAndFilm(Account account, Film film);
    // Đếm số lượng comment theo filmId
    Long countByFilmId(Integer filmId);
    List<Comment> findByFilm(Film film);
    Page<Comment> findByContentContainingIgnoreCase(String keyword, Pageable pageable);

}

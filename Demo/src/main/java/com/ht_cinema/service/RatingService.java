package com.ht_cinema.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ht_cinema.model.Account;
import com.ht_cinema.model.Film;
import com.ht_cinema.model.Rating;
import com.ht_cinema.repository.BookingRepository;
import com.ht_cinema.repository.RatingRepository;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private BookingRepository bookingRepository;

    // ✅ Thêm hoặc cập nhật đánh giá
    public Rating addOrUpdateRating(Account account, Film film, int star) {
        // Kiểm tra user có booking cho phim chưa
    	long count = bookingRepository.countBooking(account.getId(), film.getId());
    	if (count == 0) {
    	    throw new IllegalStateException("Bạn cần xem phim mới có thể đánh giá.");
    	}

        // Nếu đã có rating thì cập nhật, nếu chưa thì tạo mới
        return ratingRepository.findByAccountAndFilm(account, film)
                .map(r -> {
                    r.setStar(star);
                    r.setCreatedAt(LocalDateTime.now());
                    return ratingRepository.save(r);
                })
                .orElseGet(() -> {
                    Rating newRating = Rating.builder()
                            .account(account)
                            .film(film)
                            .star(star)
                            .createdAt(LocalDateTime.now())
                            .build();
                    return ratingRepository.save(newRating);
                });
    }
    
    //  Hàm lấy số sao người dùng đã đánh giá
    public Integer findUserRating(Integer userId, Integer filmId) {
        Account account = new Account();
        account.setId(userId);

        Film film = new Film();
        film.setId(filmId);

        return ratingRepository.findByAccountAndFilm(account, film)
                               .map(Rating::getStar)
                               .orElse(null);
    }

    // ✅ Lấy danh sách rating theo phim
    public List<Rating> getRatingsByFilm(Film film) {
        return ratingRepository.findByFilmId(film.getId());
    }

    // ✅ Tính điểm trung bình
    public double getAverageStar(Integer filmId) {
        Double avg = ratingRepository.getAverageStar(filmId);
        return avg != null ? avg : 0.0;
    }

    // ✅ Đếm số lượng đánh giá
    public Long getCount(Integer filmId) {
        return ratingRepository.getCount(filmId);
    }
}

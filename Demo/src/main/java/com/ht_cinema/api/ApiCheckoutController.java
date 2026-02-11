package com.ht_cinema.api;

import java.util.List;
import org.springframework.web.bind.annotation.*;

import com.ht_cinema.movieticket.model.BookingInfo;
import com.ht_cinema.movieticket.model.Movie;

@RestController
@RequestMapping("/api/booking")
public class ApiCheckoutController {

    @GetMapping("/checkout")
    public BookingInfo getCheckoutInfo(
            @RequestParam int movieId,
            @RequestParam List<String> seats) {

        // Lấy thông tin phim (demo)
		Movie movie = new Movie(
                movieId,
                "The Hobbit: An Unexpected Journey",
                "Adventure | Fantasy | Drama",
                "169 min",
                "Bilbo joins a quest to reclaim the Dwarf Kingdom from Smaug the dragon.",
                "https://i.imgur.com/rK0p6sW.jpg",
                5.0,
                "2013-12-14",
                "https://www.youtube.com/embed/SDnYMbYB-nU"
        );

        double pricePerSeat = 40.0;
        double total = seats.size() * pricePerSeat;

        return new BookingInfo(movie, seats, pricePerSeat, total, "CGV Vincom Đồng Khởi", "21:30, 10/02/2026");
    }

    @PostMapping("/confirm")
    public String confirmBooking(@RequestBody BookingInfo info) {
        // Thực tế: lưu vào DB, gửi mail, tạo vé PDF...
        return "✅ Đặt vé thành công cho " + info.getSeats().size() + " ghế!";
    }
}

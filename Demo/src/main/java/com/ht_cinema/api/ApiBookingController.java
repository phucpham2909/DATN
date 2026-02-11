package com.ht_cinema.api;


import org.springframework.web.bind.annotation.*;

import com.ht_cinema.movieticket.model.Movie;
import com.ht_cinema.movieticket.model.Seat;
import com.ht_cinema.movieticket.model.SeatRow;
import com.ht_cinema.movieticket.model.Ticket;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/booking")
public class ApiBookingController {

	@GetMapping("/seats")
    public List<SeatRow> getSeats(@RequestParam(defaultValue = "1") int movieId) {
        // ... (phần code cũ giữ nguyên)
        return List.of(); // placeholder
    }

    // ✅ API thanh toán thành công, trả thông tin vé
    @GetMapping("/success")
    public Ticket getTicket(
            @RequestParam int movieId,
            @RequestParam List<String> seats
    ) {
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

        String ticketId = "T" + System.currentTimeMillis();
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        return new Ticket(ticketId, movie, seats, "CGV Vincom Đồng Khởi", "21:30, 10/02/2026", time, "Đã thanh toán");
    }
}

package com.ht_cinema.api;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/booking")
public class ApiSeatController {

    @GetMapping("/seat")
    public List<Map<String, Object>> getSeats(@RequestParam int movieId) {
        // Mô phỏng dữ liệu ghế chia theo hàng
        List<Map<String, Object>> rows = new ArrayList<>();

        for (char row = 'A'; row <= 'E'; row++) {
            List<Map<String, Object>> seats = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                seats.add(Map.of(
                        "id", row + String.valueOf(i),
                        "booked", Math.random() < 0.2, // 20% ghế đã đặt
                        "price", (row <= 'C') ? 50 : 70 // giá tùy hàng
                ));
            }
            rows.add(Map.of(
                    "rowLabel", String.valueOf(row),
                    "seats", seats
            ));
        }
        return rows;
    }
}

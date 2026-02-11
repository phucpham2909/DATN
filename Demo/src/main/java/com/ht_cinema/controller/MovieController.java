package com.ht_cinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/movies")
public class MovieController {

    // ✅ Hiển thị trang chi tiết phim
    @GetMapping("/{id}")
    public String showMovieDetail(@PathVariable("id") Long id) {
        // Không cần truyền dữ liệu sang model — JS trong HTML sẽ tự fetch API
        return "movie/detail"; // -> /templates/movie/detail.html
    }
}

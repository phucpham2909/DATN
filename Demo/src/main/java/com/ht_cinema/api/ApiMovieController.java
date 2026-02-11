package com.ht_cinema.api;
import com.ht_cinema.movieticket.model.Comment;
import com.ht_cinema.movieticket.model.Movie;
import com.ht_cinema.movieticket.model.MovieDetail;
import com.ht_cinema.movieticket.model.Showtime;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class ApiMovieController {

    @GetMapping
    public List<Movie> getAllMovies() {
        return List.of(
            new Movie(1, "The Hobbit: An Unexpected Journey", "Adventure | Fantasy | Drama", "169 min",
                    "Bilbo joins a quest to reclaim the Dwarf Kingdom from Smaug the dragon.",
                    "https://www.tvinsider.com/show/the-hobbit-an-unexpected-journey", 5.0, "2013-12-14", "https://www.youtube.com/embed/SDnYMbYB-nU"),
            new Movie(2, "Gravity", "Sci-Fi | Thriller | Drama", "91 min",
                    "A survival story of two astronauts stranded in space.",
                    "https://i.imgur.com/mY0Vx8e.jpg", 4.1, "2013-09-27", "https://www.youtube.com/embed/OiTiKOy59o4")
        );
    }

    @GetMapping("/{id}")
    public MovieDetail getMovieDetail(@PathVariable int id) {
        Movie movie = new Movie(id, "The Hobbit: An Unexpected Journey",
                "Adventure | Fantasy | Drama", "169 min",
                "Bilbo joins a quest to reclaim the Dwarf Kingdom from Smaug the dragon.",
                "https://i.imgur.com/rK0p6sW.jpg", 5.0, "2013-12-14", "https://www.youtube.com/embed/SDnYMbYB-nU");

        List<Showtime> showtimes = List.of(
                new Showtime("Cineworld", List.of("09:40", "13:45", "19:50")),
                new Showtime("Empire", List.of("10:45", "16:00", "21:15")),
                new Showtime("Odeon", List.of("11:30", "17:00", "22:30"))
        );

        List<Comment> comments = List.of(
                new Comment("Roberta Intel", "Tuyệt vời, xem lại vẫn xúc động!", "2026-02-01 09:30"),
                new Comment("Olia Gosha", "Âm nhạc và hình ảnh quá đẹp!", "2026-02-02 11:10")
        );

        return new MovieDetail(movie, showtimes, comments);
    }
}
package com.ht_cinema.movieticket.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDetail {
	private Movie movie;
	private List<Showtime> showtimes;
	private List<Comment> comments;
}

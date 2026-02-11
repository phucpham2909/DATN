package com.ht_cinema.movieticket.model;

import java.util.List;

import com.ht_cinema.model.Account;
import com.ht_cinema.model.Booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
	 private int id;
	    private String title;
	    private String genre;
	    private String duration;
	    private String description;
	    private String image;
	    private double rating;
	    private String releaseDate;
	    private String trailer;

}

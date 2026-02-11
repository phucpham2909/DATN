package com.ht_cinema.movieticket.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Showtime {
    private String cinema;
    private List<String> times;
}

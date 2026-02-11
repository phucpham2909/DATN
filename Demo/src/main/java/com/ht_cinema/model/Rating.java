package com.ht_cinema.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ratings")
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne @JoinColumn(name="acc_id")
    private Account account;

    @ManyToOne @JoinColumn(name="film_id")
    private Film film;

    @Column(nullable = false)
    private int star;

    private LocalDateTime createdAt;
}

package com.ht_cinema.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SeatMessage {
    private Integer seatId;
    private Integer suatChieuId;
    private String action;
    private Integer accountId;
}

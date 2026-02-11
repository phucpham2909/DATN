package com.ht_cinema.controller;

import com.ht_cinema.dto.SeatMessage;
import com.ht_cinema.repository.SeatsRepository;
import com.ht_cinema.service.SeatHoldService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
@Slf4j
public class SeatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final SeatHoldService seatHoldService;
    private final SeatsRepository seatsRepository;
    
    @PostMapping("/hold-seat")
    public ResponseEntity<?> holdSeat(@RequestParam Integer seatId,
                                      @RequestParam Integer suatChieuId) {
        Integer accountId = seatHoldService.getAccountId(); 
        
        if (seatHoldService.tryHoldSeat(seatId, suatChieuId)) {
            SeatMessage msg = new SeatMessage(seatId, suatChieuId, "hold", accountId);
            messagingTemplate.convertAndSend("/topic/seat-status/" + suatChieuId, msg);
            return ResponseEntity.ok(Map.of("success", true));
        } else {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Ghế đang được giữ bởi người khác"
            ));
        }
    }
    @PostMapping("/release-seat")
    public ResponseEntity<?> releaseSeat(@RequestParam Integer seatId,
                                         @RequestParam Integer suatChieuId) {
        Integer accountId = seatHoldService.getAccountId(); 

        seatHoldService.release(seatId, suatChieuId);
        
        SeatMessage msg = new SeatMessage(seatId, suatChieuId, "release", accountId);
        messagingTemplate.convertAndSend("/topic/seat-status/" + suatChieuId, msg);

        return ResponseEntity.ok(Map.of("success", true));
    }
    
    @Transactional
//    @PostMapping("/confirm")
    public ResponseEntity<?> confirmBooking(@RequestParam Integer suatChieuId,
                                            @RequestParam List<Integer> seatIds) {
        Integer accountId = seatHoldService.getAccountId();

        if (seatIds == null || seatIds.isEmpty()) {
            return ResponseEntity.badRequest().body("Vui lòng chọn ghế.");
        }

        for (Integer seatId : seatIds) {
            if (seatHoldService.isSeatCurrentlyHeld(seatId, suatChieuId)) {
                log.warn("Booking failed for SeatId={}: currently held.", seatId);
                continue; 
            }
            
            seatsRepository.findById(seatId).ifPresent(seat -> {
                if (seat.getStatus() == 1) {
                    seat.setStatus(0);
                    seatsRepository.save(seat);
                    SeatMessage msg = new SeatMessage(seatId, suatChieuId, "booked", accountId);
                    messagingTemplate.convertAndSend("/topic/seat-status/" + suatChieuId, msg);
                }
            });
            
            seatHoldService.release(seatId, suatChieuId);
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đặt vé thành công!"
        ));
    }
}
package com.ht_cinema.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ht_cinema.dto.SeatMessage;
import com.ht_cinema.model.Seats;
import com.ht_cinema.repository.SeatsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/admin/seats")
@RequiredArgsConstructor
@Slf4j
public class AdminSeatApiController {

 private final SimpMessagingTemplate messagingTemplate;
 private final SeatsRepository seatsRepository;

@PostMapping("/update-status/{seatId}")
public ResponseEntity<?> updateSeatStatus(@PathVariable Integer seatId, @RequestParam Integer newStatus, @RequestParam Integer suatChieuId) {
  
  Seats seat = seatsRepository.findById(seatId)
          .orElseThrow(() -> new IllegalArgumentException("Seat not found"));

  seat.setStatus(newStatus); 
  seatsRepository.save(seat);
  
  String action;
  if (newStatus == -1) {
      // Ghế bị Hỏng/Ẩn (User sẽ thấy màu xám: .seat-broken)
      action = "out_of_service"; 
  } else if (newStatus == 0) {
      // Ghế được phục hồi (User sẽ thấy màu Thường/VIP lại)
      action = "restored"; 
  } else {
      return ResponseEntity.badRequest().body("Trạng thái không hợp lệ.");
  }
  
  SeatMessage msg = new SeatMessage(seatId, suatChieuId, action, 0); 
  
  log.info("Admin Action: Seat ID {} status changed to {}. Sending message: {}", seatId, newStatus, action);
  messagingTemplate.convertAndSend("/topic/seat-status/" + suatChieuId, msg); 
  
  return ResponseEntity.ok().build();
}
}
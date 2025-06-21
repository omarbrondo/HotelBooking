package com.app.HotelBooking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.app.HotelBooking.dto.DashboardDTO;
import com.app.HotelBooking.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

  private final DashboardService svc;
  public DashboardController(DashboardService svc) {
    this.svc = svc;
  }

  @GetMapping
  public ResponseEntity<DashboardDTO> stats() {
    return ResponseEntity.ok(svc.getStats());
  }
}

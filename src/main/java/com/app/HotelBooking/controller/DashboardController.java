// src/main/java/com/app/HotelBooking/controller/DashboardController.java
package com.app.HotelBooking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.HotelBooking.dto.DashboardDTO;
import com.app.HotelBooking.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

  private final DashboardService dashboardService;
  public DashboardController(DashboardService svc) {
    this.dashboardService = svc;
  }

  @GetMapping
  public ResponseEntity<DashboardDTO> stats() {
    return ResponseEntity.ok(dashboardService.getStats());
  }
}

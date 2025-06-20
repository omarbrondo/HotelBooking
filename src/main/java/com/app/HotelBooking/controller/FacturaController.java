package com.app.HotelBooking.controller;

import com.app.HotelBooking.model.Factura;
import com.app.HotelBooking.service.CheckoutService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

  @Autowired
  private CheckoutService checkoutService;

  @PostMapping("/{reservaId}")
  public ResponseEntity<Factura> confirmarCheckout(@PathVariable Long reservaId) {
    Factura fac = checkoutService.checkout(reservaId);
    return ResponseEntity.ok(fac);
  }
}

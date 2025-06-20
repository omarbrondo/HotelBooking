package com.app.HotelBooking.controller;

import com.app.HotelBooking.model.Factura;
import com.app.HotelBooking.service.CheckoutService;
import com.app.HotelBooking.service.FacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    private final CheckoutService checkoutService;
    private final FacturaService facturaService;

    @Autowired
    public FacturaController(CheckoutService checkoutService,
                             FacturaService facturaService) {
        this.checkoutService = checkoutService;
        this.facturaService = facturaService;
    }

    // POST /api/facturas/{reservaId} → confirma checkout y genera factura
    @PostMapping("/{reservaId}")
    public ResponseEntity<Factura> confirmarCheckout(@PathVariable Long reservaId) {
        Factura fac = checkoutService.checkout(reservaId);
        return ResponseEntity.ok(fac);
    }

    // GET /api/facturas → devuelve todas las facturas
    @GetMapping
    public ResponseEntity<List<Factura>> listarFacturas() {
        List<Factura> todas = facturaService.listarTodas();
        return ResponseEntity.ok(todas);
    }
}

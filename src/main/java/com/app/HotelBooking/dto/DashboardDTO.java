package com.app.HotelBooking.dto;

import java.math.BigDecimal;
import java.util.List;

public class DashboardDTO {
  public List<ItemCount> topProductos;
  public List<ItemCount> topHabitaciones;
  public List<ItemCount> topFechas;
  public BigDecimal ingresoTotal;           // <— nuevo

  public static class ItemCount {
    public String key;
    public Long count;
    public ItemCount() {}
    public ItemCount(String key, Long count) {
      this.key   = key;
      this.count = count;
    }
  }
}

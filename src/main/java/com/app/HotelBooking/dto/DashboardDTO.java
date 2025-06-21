// src/main/java/com/app/HotelBooking/dto/DashboardDTO.java
package com.app.HotelBooking.dto;

import java.util.List;

public class DashboardDTO {
  public List<ItemCount> topProductos;
  public List<ItemCount> topHabitaciones;
  public List<ItemCount> topFechas;

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

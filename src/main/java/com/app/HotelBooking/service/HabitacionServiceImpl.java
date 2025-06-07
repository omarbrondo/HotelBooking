package com.app.HotelBooking.service;

import com.app.HotelBooking.model.Habitacion;
import com.app.HotelBooking.repository.HabitacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HabitacionServiceImpl implements HabitacionService {

    private final HabitacionRepository habitacionRepository;

    public HabitacionServiceImpl(HabitacionRepository habitacionRepository) {
        this.habitacionRepository = habitacionRepository;
    }

    @Override
    public List<Habitacion> obtenerHabitacionesLibres() {
        return habitacionRepository.findByEstado("libre");
    }
}

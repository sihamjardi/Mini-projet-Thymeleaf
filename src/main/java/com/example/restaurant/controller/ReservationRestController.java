package com.example.restaurant.controller;

import com.example.restaurant.model.Reservation;
import com.example.restaurant.service.ReservationService;
import com.example.restaurant.service.TableRestoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin
public class ReservationRestController {
    private final ReservationService reservationService;
    private final TableRestoService tableRestoService;

    public ReservationRestController(ReservationService reservationService, TableRestoService tableRestoService) {
        this.reservationService = reservationService;
        this.tableRestoService = tableRestoService;
    }

    @GetMapping
    public Iterable<Reservation> list(@RequestParam(value = "statut", required = false) String statut,
                                      @RequestParam(value = "zone", required = false) String zone,
                                      @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                      @RequestParam(value = "min", required = false) Integer min,
                                      @RequestParam(value = "max", required = false) Integer max) {
        if (statut != null) return reservationService.filterByStatut(statut);
        if (zone != null) return reservationService.filterByZone(zone);
        if (date != null) return reservationService.filterByDate(date);
        if (min != null && max != null) return reservationService.filterByGroupSize(min, max);
        return reservationService.listAll();
    }

    @GetMapping("/stats")
    public Map<String, Object> stats(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        int totalSeats = (int) tableRestoService.totalSeats();
        double matin = reservationService.occupancyRateForService(date, "matin", totalSeats);
        double soir = reservationService.occupancyRateForService(date, "soir", totalSeats);
        double noShow = reservationService.noShowRate(date);
        Map<String, Object> map = new HashMap<>();
        map.put("date", date.toString());
        map.put("matin", matin);
        map.put("soir", soir);
        map.put("noShow", noShow);
        map.put("totalSeats", totalSeats);
        return map;
    }
}




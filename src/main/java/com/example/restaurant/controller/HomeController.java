package com.example.restaurant.controller;

import com.example.restaurant.model.Reservation;
import com.example.restaurant.service.ClientService;
import com.example.restaurant.service.TableRestoService;
import com.example.restaurant.service.ReservationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    private final ClientService clientService;
    private final TableRestoService tableRestoService;
    private final ReservationService reservationService;

    public HomeController(ClientService clientService, TableRestoService tableRestoService, ReservationService reservationService) {
        this.clientService = clientService;
        this.tableRestoService = tableRestoService;
        this.reservationService = reservationService;
    }

    @GetMapping("/")
    public String home(@RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                       Model model) {
        LocalDate d = date != null ? date : LocalDate.now();
        int totalSeats = (int) tableRestoService.totalSeats();
        double matin = reservationService.occupancyRateForService(d, "matin", totalSeats);
        double soir = reservationService.occupancyRateForService(d, "soir", totalSeats);
        int matinReserved = reservationService.reservedSeatsForService(d, "matin");
        int soirReserved = reservationService.reservedSeatsForService(d, "soir");
        
        // Counts
        int clientCount = 0; 
        for (var c : clientService.listAll()) clientCount++;
        int tableCount = 0; 
        for (var t : tableRestoService.listAll()) tableCount++;
        
        // Reservations du jour
        Iterable<Reservation> todayReservations = reservationService.filterByDate(d);
        int todayReservationCount = 0;
        List<Map<String, Object>> todayReservationsList = new ArrayList<>();
        for (Reservation r : todayReservations) {
            todayReservationCount++;
            Map<String, Object> res = new HashMap<>();
            res.put("clientName", r.getClient() != null ? r.getClient().getNom() : "N/A");
            res.put("dateHeure", r.getPk() != null ? r.getPk().getDateHeure() : null);
            res.put("nbCouverts", r.getNbCouverts());
            res.put("statut", r.getStatut());
            todayReservationsList.add(res);
        }
        
        // Overall occupancy
        double overallOccupancy = (matinReserved + soirReserved) > 0 ?
            ((matin + soir) / 2) * 100 : 0;
        
        // No-show rate
        double noShowRate = reservationService.noShowRate(d);
        
        // Stats pour la semaine
        Map<String, Integer> weekStats = new HashMap<>();
        for (int i = 0; i < 7; i++) {
            LocalDate day = d.minusDays(i);
            int count = 0;
            for (Reservation res : reservationService.filterByDate(day)) {
                count++;
            }
            weekStats.put(day.toString(), count);
        }
        
        // Stats par statut
        Map<String, Integer> statusStats = new HashMap<>();
        for (Reservation res : reservationService.listAll()) {
            String statut = res.getStatut() != null ? res.getStatut() : "UNKNOWN";
            statusStats.put(statut, statusStats.getOrDefault(statut, 0) + 1);
        }

        model.addAttribute("date", d);
        model.addAttribute("totalSeats", totalSeats);
        model.addAttribute("matin", matin);
        model.addAttribute("soir", soir);
        model.addAttribute("matinReserved", matinReserved);
        model.addAttribute("soirReserved", soirReserved);
        model.addAttribute("clientCount", clientCount);
        model.addAttribute("tableCount", tableCount);
        model.addAttribute("todayReservationCount", todayReservationCount);
        model.addAttribute("todayReservations", todayReservationsList);
        model.addAttribute("overallOccupancy", overallOccupancy);
        model.addAttribute("noShowRate", noShowRate);
        model.addAttribute("weekStats", weekStats);
        model.addAttribute("statusStats", statusStats);
        
        return "index";
    }
}



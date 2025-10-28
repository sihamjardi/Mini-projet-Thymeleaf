package com.example.restaurant.controller;

import com.example.restaurant.model.Reservation;
import com.example.restaurant.model.ReservationPK;
import com.example.restaurant.model.Client;
import com.example.restaurant.model.TableResto;
import com.example.restaurant.service.ReservationService;
import com.example.restaurant.service.TableRestoService;
import com.example.restaurant.service.ClientService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/reservations")
public class ReservationController {
    private final ReservationService reservationService;
    private final TableRestoService tableRestoService;
    private final ClientService clientService;

    public ReservationController(ReservationService reservationService, TableRestoService tableRestoService, ClientService clientService) {
        this.reservationService = reservationService;
        this.tableRestoService = tableRestoService;
        this.clientService = clientService;
    }

    @GetMapping
    public String list(@RequestParam(value = "statut", required = false) String statut,
                       @RequestParam(value = "zone", required = false) String zone,
                       @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                       @RequestParam(value = "min", required = false) Integer min,
                       @RequestParam(value = "max", required = false) Integer max,
                       @RequestParam(value = "success", required = false) String success,
                       Model model) {
        Iterable<Reservation> data;
        boolean hasAny = (statut != null && !statut.isEmpty()) ||
                (zone != null && !zone.isEmpty()) ||
                date != null ||
                (min != null) ||
                (max != null);
        if (hasAny) data = reservationService.filterCombined(statut != null && statut.isEmpty() ? null : statut,
                zone != null && zone.isEmpty() ? null : zone,
                date,
                min,
                max);
        else data = reservationService.listAll();
        model.addAttribute("reservations", data);
        model.addAttribute("statut", statut);
        model.addAttribute("zone", zone);
        model.addAttribute("date", date);
        model.addAttribute("min", min);
        model.addAttribute("max", max);
        model.addAttribute("success", success);
        model.addAttribute("page", "reservations");
        return "reservations/list";
    }

    @GetMapping("/stats")
    public String stats(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, Model model) {
        model.addAttribute("page", "stats");
        int totalSeats = (int) tableRestoService.totalSeats();
        double matin = reservationService.occupancyRateForService(date, "matin", totalSeats);
        double soir = reservationService.occupancyRateForService(date, "soir", totalSeats);
        double noShow = reservationService.noShowRate(date);
        int matinReserved = reservationService.reservedSeatsForService(date, "matin");
        int soirReserved = reservationService.reservedSeatsForService(date, "soir");
        
        // Stats supplémentaires
        long totalReservations = reservationService.filterByDate(date).spliterator().getExactSizeIfKnown();
        long totalClients = clientService.listAll().spliterator().getExactSizeIfKnown();
        long totalTables = tableRestoService.listAll().spliterator().getExactSizeIfKnown();
        long confirmed = reservationService.filterByStatut("CONFIRMEE").spliterator().getExactSizeIfKnown();
        long cancelled = reservationService.filterByStatut("ANNULEE").spliterator().getExactSizeIfKnown();
        
        model.addAttribute("date", date);
        model.addAttribute("matin", matin);
        model.addAttribute("soir", soir);
        model.addAttribute("noShow", noShow);
        model.addAttribute("totalSeats", totalSeats);
        model.addAttribute("matinReserved", matinReserved);
        model.addAttribute("soirReserved", soirReserved);
        model.addAttribute("totalReservations", totalReservations);
        model.addAttribute("totalClients", totalClients);
        model.addAttribute("totalTables", totalTables);
        model.addAttribute("confirmed", confirmed);
        model.addAttribute("cancelled", cancelled);
        return "reservations/stats";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("reservation", new Reservation());
        model.addAttribute("tables", tableRestoService.listAll());
        model.addAttribute("clients", clientService.listAll());
        model.addAttribute("page", "reservations");
        return "reservations/form";
    }

    @PostMapping
    public String create(@RequestParam long clientId,
                         @RequestParam long tableId,
                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime dateHeure,
                         @RequestParam int nbCouverts,
                         @RequestParam String statut) {
        Client c = clientService.get(clientId);
        TableResto t = tableRestoService.get(tableId);
        ReservationPK pk = new ReservationPK();
        pk.setClient(c.getId());
        pk.setTablersto(t.getId());
        pk.setDateHeure(dateHeure);
        Reservation r = new Reservation(pk, nbCouverts, statut, t, c);
        reservationService.save(r);
        return "redirect:/reservations?success=create";
    }

    @GetMapping("/edit")
    public String editForm(@RequestParam long clientId,
                           @RequestParam long tableId,
                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime dateHeure,
                           Model model) {
        ReservationPK pk = new ReservationPK();
        pk.setClient(clientId);
        pk.setTablersto(tableId);
        pk.setDateHeure(dateHeure);
        Reservation r = reservationService.get(pk);
        model.addAttribute("reservation", r);
        model.addAttribute("tables", tableRestoService.listAll());
        model.addAttribute("clients", clientService.listAll());
        model.addAttribute("page", "reservations");
        return "reservations/edit";
    }

    @PostMapping("/update")
    public String update(@RequestParam long originalClientId,
                         @RequestParam long originalTableId,
                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime originalDateHeure,
                         @RequestParam long clientId,
                         @RequestParam long tableId,
                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime dateHeure,
                         @RequestParam int nbCouverts,
                         @RequestParam String statut) {
        // Delete old reservation
        ReservationPK oldPk = new ReservationPK();
        oldPk.setClient(originalClientId);
        oldPk.setTablersto(originalTableId);
        oldPk.setDateHeure(originalDateHeure);
        reservationService.delete(oldPk);
        
        // Create new reservation
        Client c = clientService.get(clientId);
        TableResto t = tableRestoService.get(tableId);
        ReservationPK pk = new ReservationPK();
        pk.setClient(c.getId());
        pk.setTablersto(t.getId());
        pk.setDateHeure(dateHeure);
        Reservation r = new Reservation(pk, nbCouverts, statut, t, c);
        reservationService.save(r);
        return "redirect:/reservations?success=update";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam long clientId,
                         @RequestParam long tableId,
                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime dateHeure,
                         Model model) {
        ReservationPK pk = new ReservationPK();
        pk.setClient(clientId);
        pk.setTablersto(tableId);
        pk.setDateHeure(dateHeure);
        reservationService.delete(pk);
        return "redirect:/reservations?success=delete";
    }

}

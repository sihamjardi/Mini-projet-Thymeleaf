package com.example.restaurant.service;

import com.example.restaurant.model.Reservation;
import com.example.restaurant.model.ReservationPK;
import com.example.restaurant.model.StatutReservation;
import com.example.restaurant.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Iterable<Reservation> listAll() { return reservationRepository.findAll(); }

    public Reservation get(ReservationPK pk) { return reservationRepository.findById(pk).orElse(null); }

    public Iterable<Reservation> filterByStatut(StatutReservation statut) { return reservationRepository.findByStatut(statut); }

    public Iterable<Reservation> filterByDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);
        return reservationRepository.findBetweenDates(start, end);
    }

    public Iterable<Reservation> filterByZone(String zone) { return reservationRepository.findByZone(zone); }

    public Iterable<Reservation> filterByGroupSize(int min, int max) { return reservationRepository.findByGroupSizeBetween(min, max); }

    public Iterable<Reservation> filterCombined(StatutReservation statut, String zone, LocalDate date, Integer min, Integer max) {
        java.time.LocalDateTime start = null;
        java.time.LocalDateTime end = null;
        if (date != null) {
            start = date.atStartOfDay();
            end = date.atTime(java.time.LocalTime.MAX);
        }
        return reservationRepository.filter(statut, zone, start, end, min, max);
    }

    public Reservation save(Reservation reservation) { return reservationRepository.save(reservation); }

    public void delete(ReservationPK pk) { reservationRepository.deleteById(pk); }

    // Taux d'occupation (%) par service
    public double occupancyRateForService(LocalDate date, String service, int totalSeats) {
        if (totalSeats <= 0) return 0.0;

        LocalDateTime start, end;

        switch (service.toLowerCase()) {
            case "matin": start = date.atTime(8, 0); end = date.atTime(15, 0); break;
            case "soir": start = date.atTime(15, 0); end = date.atTime(23, 0); break;
            default: start = date.atStartOfDay(); end = date.atTime(LocalTime.MAX);
        }

        int reserved = 0;
        for (Reservation r : reservationRepository.findBetweenDates(start, end)) {
            // On compte uniquement les réservations confirmées
            if (r.getStatut() == StatutReservation.CONFIRME) {
                reserved += r.getNbCouverts();
            }
        }

        return ((double) reserved / totalSeats) * 100;
    }

    // Nombre de sièges réservés par service
    public int reservedSeatsForService(LocalDate date, String service) {
        LocalDateTime start, end;

        switch (service.toLowerCase()) {
            case "matin": start = date.atTime(8, 0); end = date.atTime(15, 0); break;
            case "soir": start = date.atTime(15, 0); end = date.atTime(23, 0); break;
            default: start = date.atStartOfDay(); end = date.atTime(LocalTime.MAX);
        }

        int reserved = 0;
        for (Reservation r : reservationRepository.findBetweenDates(start, end)) {
            if (r.getStatut() == StatutReservation.CONFIRME) {
                reserved += r.getNbCouverts();
            }
        }

        return reserved;
    }

    // Taux de no-show (uniquement sur les réservations confirmées)
    public double noShowRate(LocalDate date) {
        Iterable<Reservation> today = filterByDate(date);
        long totalConfirmed = 0;
        long noShowCount = 0;

        for (Reservation r : today) {
            if (r.getStatut() == StatutReservation.CONFIRME || r.getStatut() == StatutReservation.NO_SHOW) {
                totalConfirmed++;
                if (r.getStatut() == StatutReservation.NO_SHOW) {
                    noShowCount++;
                }
            }
        }

        return totalConfirmed == 0 ? 0.0 : ((double) noShowCount / totalConfirmed) * 100;
    }




}

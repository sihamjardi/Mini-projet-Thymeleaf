package com.example.restaurant.service;

import com.example.restaurant.model.Reservation;
import com.example.restaurant.model.ReservationPK;
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

    public Iterable<Reservation> filterByStatut(String statut) { return reservationRepository.findByStatut(statut); }

    public Iterable<Reservation> filterByDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);
        return reservationRepository.findBetweenDates(start, end);
    }

    public Iterable<Reservation> filterByZone(String zone) { return reservationRepository.findByZone(zone); }

    public Iterable<Reservation> filterByGroupSize(int min, int max) { return reservationRepository.findByGroupSizeBetween(min, max); }

    public Iterable<Reservation> filterCombined(String statut, String zone, LocalDate date, Integer min, Integer max) {
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

    public double occupancyRateForService(LocalDate date, String service, int totalSeats) {
        LocalDateTime start;
        LocalDateTime end;

        switch (service.toLowerCase()) {
            case "matin": // 8h à 15h
                start = date.atTime(8, 0);
                end = date.atTime(15, 0);
                break;
            case "soir": // 15h à 23h
                start = date.atTime(15, 0);
                end = date.atTime(23, 0);
                break;
            default: // toute la journée
                start = date.atStartOfDay();
                end = date.atTime(LocalTime.MAX);
        }

        int reserved = 0;
        for (Reservation r : reservationRepository.findBetweenDates(start, end)) {
            reserved += r.getNbCouverts();
        }

        if (totalSeats <= 0) return 0.0;
        double rate = (double) reserved / totalSeats;
        return Math.max(0.0, Math.min(1.0, rate));
    }

    public int reservedSeatsForService(LocalDate date, String service) {
        LocalDateTime start;
        LocalDateTime end;

        switch (service.toLowerCase()) {
            case "matin":
                start = date.atTime(8, 0);
                end = date.atTime(15, 0);
                break;
            case "soir":
                start = date.atTime(15, 0);
                end = date.atTime(23, 0);
                break;
            default:
                start = date.atStartOfDay();
                end = date.atTime(LocalTime.MAX);
        }

        int reserved = 0;
        for (Reservation r : reservationRepository.findBetweenDates(start, end)) {
            reserved += r.getNbCouverts();
        }
        return reserved;
    }



    public double noShowRate(LocalDate date) {
        long total = filterByDate(date).spliterator().getExactSizeIfKnown();
        if (total == 0) return 0;
        long noShowCount = filterByStatut("NO_SHOW").spliterator().getExactSizeIfKnown();
        return (double) noShowCount / total;
    }

}

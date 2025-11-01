package com.example.restaurant.repository;

import com.example.restaurant.model.Reservation;
import com.example.restaurant.model.ReservationPK;
import com.example.restaurant.model.StatutReservation;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ReservationRepository extends CrudRepository<Reservation, ReservationPK> {

    // Trouver par statut (enum)
    Iterable<Reservation> findByStatut(StatutReservation statut);

    // Filtrer par dates
    @Query("select r from Reservation r where r.pk.dateHeure between :start and :end")
    Iterable<Reservation> findBetweenDates(@Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end);

    // Filtrer par zone
    @Query("select r from Reservation r where r.tablersto.zone = :zone")
    Iterable<Reservation> findByZone(@Param("zone") String zone);

    // Filtrer par taille de groupe
    @Query("select r from Reservation r where r.nbCouverts >= :min and r.nbCouverts <= :max")
    Iterable<Reservation> findByGroupSizeBetween(@Param("min") int min,
                                                 @Param("max") int max);

    // Filtrage dynamique avec enum
    @Query("select r from Reservation r " +
            "where (:statut is null or r.statut = :statut) " +
            "and (:zone is null or r.tablersto.zone = :zone) " +
            "and ((:start is null or :end is null) or r.pk.dateHeure between :start and :end) " +
            "and (:min is null or r.nbCouverts >= :min) " +
            "and (:max is null or r.nbCouverts <= :max)")
    Iterable<Reservation> filter(@Param("statut") StatutReservation statut,
                                 @Param("zone") String zone,
                                 @Param("start") LocalDateTime start,
                                 @Param("end") LocalDateTime end,
                                 @Param("min") Integer min,
                                 @Param("max") Integer max);

    @Modifying
    @Transactional
    @Query("DELETE FROM Reservation r WHERE r.tablersto.id = :tableId")
    void deleteByTableId(@Param("tableId") Long tableId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Reservation r WHERE r.client.id = :clientId")
    void deleteByClientId(@Param("clientId") Long clientId);
}

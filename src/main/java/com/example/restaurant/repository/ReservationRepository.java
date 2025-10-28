package com.example.restaurant.repository;

import com.example.restaurant.model.Reservation;
import com.example.restaurant.model.ReservationPK;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends CrudRepository<Reservation, ReservationPK> {
    Iterable<Reservation> findByStatut(String statut);

    @Query("select r from Reservation r where r.pk.dateHeure between :start and :end")
    Iterable<Reservation> findBetweenDates(@Param("start") java.time.LocalDateTime start,
                                           @Param("end") java.time.LocalDateTime end);

    @Query("select r from Reservation r where r.tablersto.zone = :zone")
    Iterable<Reservation> findByZone(@Param("zone") String zone);

    @Query("select r from Reservation r where r.nbCouverts >= :min and r.nbCouverts <= :max")
    Iterable<Reservation> findByGroupSizeBetween(@Param("min") int min,
                                                 @Param("max") int max);

    @Query("select r from Reservation r \n" +
            "where (:statut is null or r.statut = :statut) \n" +
            "and (:zone is null or r.tablersto.zone = :zone) \n" +
            "and ((:start is null or :end is null) or r.pk.dateHeure between :start and :end) \n" +
            "and (:min is null or r.nbCouverts >= :min) \n" +
            "and (:max is null or r.nbCouverts <= :max)")
    Iterable<Reservation> filter(@Param("statut") String statut,
                                 @Param("zone") String zone,
                                 @Param("start") java.time.LocalDateTime start,
                                 @Param("end") java.time.LocalDateTime end,
                                 @Param("min") Integer min,
                                 @Param("max") Integer max);
}

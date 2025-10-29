package com.example.restaurant.service;

import com.example.restaurant.model.TableResto;
import com.example.restaurant.repository.ReservationRepository;
import com.example.restaurant.repository.TableRestoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class TableRestoService {
    private final TableRestoRepository tableRestoRepository;
    private final ReservationRepository reservationRepository;

    public TableRestoService(TableRestoRepository tableRestoRepository,
                             ReservationRepository reservationRepository) {
        this.tableRestoRepository = tableRestoRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public void delete(Long id) {
        // Supprimer d'abord les réservations liées à cette table
        reservationRepository.deleteByTableId(id); // ✅ Appel sur l'instance, pas sur la classe

        // Supprimer ensuite la table
        tableRestoRepository.deleteById(id);
    }

    public Iterable<TableResto> listAll() { return tableRestoRepository.findAll(); }

    public long totalSeats() {
        long total = 0;
        for (TableResto t : tableRestoRepository.findAll()) {
            total += t.getPlaces();
        }
        return total;
    }

    public TableResto save(TableResto tableResto) { return tableRestoRepository.save(tableResto); }

    public TableResto get(long id) { return tableRestoRepository.findById(id).orElse(null); }
}

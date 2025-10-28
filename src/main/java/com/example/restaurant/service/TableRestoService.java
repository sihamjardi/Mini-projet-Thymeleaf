package com.example.restaurant.service;

import com.example.restaurant.model.TableResto;
import com.example.restaurant.repository.TableRestoRepository;
import org.springframework.stereotype.Service;

@Service
public class TableRestoService {
    private final TableRestoRepository tableRestoRepository;

    public TableRestoService(TableRestoRepository tableRestoRepository) {
        this.tableRestoRepository = tableRestoRepository;
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

    public void delete(long id) { tableRestoRepository.deleteById(id); }

    public TableResto get(long id) { return tableRestoRepository.findById(id).orElse(null); }
}

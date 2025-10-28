package com.example.restaurant.repository;

import com.example.restaurant.model.TableResto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TableRestoRepository extends CrudRepository<TableResto, Long> {}



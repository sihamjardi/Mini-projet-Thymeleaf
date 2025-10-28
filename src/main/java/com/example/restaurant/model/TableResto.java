package com.example.restaurant.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class TableResto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private int numero;
    @Column(name = "place")
    private int places;
    private String zone;

    @OneToMany(mappedBy = "tablersto")
    private List<Reservation> reservations;


    public TableResto(int numero, String zone, int places, List<Reservation> reservations) {
        this.numero = numero;
        this.zone = zone;
        this.places = places;
        this.reservations = reservations;
    }

    public TableResto() {}

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public int getNumero() {
        return numero;
    }
    public void setNumero(int numero) {
        this.numero = numero;
    }
    public int getPlaces() {
        return places;
    }
    public void setPlaces(int places) {
        this.places = places;
    }
    public String getZone() {
        return zone;
    }
    public void setZone(String zone) {
        this.zone = zone;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }
    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }



}

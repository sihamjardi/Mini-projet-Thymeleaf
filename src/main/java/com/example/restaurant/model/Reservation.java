package com.example.restaurant.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;


@Entity

public class Reservation {
    @EmbeddedId
    private ReservationPK pk ;
    private int nbCouverts;
    private String statut;

    @ManyToOne
    @MapsId("tablersto")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "tablersto")
    private TableResto tablersto;

    @ManyToOne
    @MapsId("client")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "client")
    private Client client;

    public Reservation() {
    }


    public Reservation(ReservationPK pk, int nbCouverts, String statut, TableResto tablersto, Client client) {
        this.pk = pk;
        this.nbCouverts = nbCouverts;
        this.statut = statut;
        this.tablersto = tablersto;
        this.client = client;
    }

    public ReservationPK getPk() {
        return pk;
    }

    public void setPk(ReservationPK pk) {
        this.pk = pk;
    }


    public int getNbCouverts() {
        return nbCouverts;
    }

    public void setNbCouverts(int nbCouverts) {
        this.nbCouverts = nbCouverts;
    }

    @Transient
    public LocalDateTime getDateHeure() { return pk != null ? pk.getDateHeure() : null; }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public TableResto getTablersto() {
        return tablersto;
    }

    public void setTablersto(TableResto tablersto) {
        this.tablersto = tablersto;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}

package com.example.restaurant.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
public class ReservationPK implements Serializable {
    private long client;
    private long tablersto;
    private LocalDateTime dateHeure;

    public long getClient() {
        return client;
    }

    public void setClient(long client) {
        this.client = client;
    }

    public long getTablersto() {
        return tablersto;
    }

    public void setTablersto(long tablersto) {
        this.tablersto = tablersto;
    }

    public LocalDateTime getDateHeure() {
        return dateHeure;
    }

    public void setDateHeure(LocalDateTime dateHeure) {
        this.dateHeure = dateHeure;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationPK that = (ReservationPK) o;
        return client == that.client && tablersto == that.tablersto && Objects.equals(dateHeure, that.dateHeure);
    }

    @Override
    public int hashCode() {
        return Objects.hash(client, tablersto, dateHeure);
    }
}

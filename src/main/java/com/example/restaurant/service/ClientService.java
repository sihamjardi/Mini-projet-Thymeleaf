package com.example.restaurant.service;

import com.example.restaurant.model.Client;
import com.example.restaurant.repository.ClientRepository;
import com.example.restaurant.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ClientService {
    private final ClientRepository clientRepository;
    private final ReservationRepository reservationRepository;


    public ClientService(ClientRepository clientRepository,
                         ReservationRepository reservationRepository) {
        this.clientRepository = clientRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public void delete(Long clientId) {
        // Supprimer toutes les réservations du client
        reservationRepository.deleteByClientId(clientId);

        // Supprimer ensuite le client
        clientRepository.deleteById(clientId);
    }
    public Iterable<Client> listAll() { return clientRepository.findAll(); }

    public Client save(Client client) { return clientRepository.save(client); }

    public void delete(long id) { clientRepository.deleteById(id); }

    public Client get(long id) { return clientRepository.findById(id).orElse(null); }
}

package com.example.restaurant.service;

import com.example.restaurant.model.Client;
import com.example.restaurant.repository.ClientRepository;
import org.springframework.stereotype.Service;

@Service
public class ClientService {
    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Iterable<Client> listAll() { return clientRepository.findAll(); }

    public Client save(Client client) { return clientRepository.save(client); }

    public void delete(long id) { clientRepository.deleteById(id); }

    public Client get(long id) { return clientRepository.findById(id).orElse(null); }
}

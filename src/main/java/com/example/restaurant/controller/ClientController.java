package com.example.restaurant.controller;

import com.example.restaurant.service.ClientService;
import com.example.restaurant.model.Client;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/clients")
public class ClientController {
    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("clients", clientService.listAll());
        model.addAttribute("page", "clients");
        return "clients/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("client", new Client());
        model.addAttribute("page", "clients");
        return "clients/form";
    }

    @PostMapping
    public String create(@ModelAttribute Client client) {
        clientService.save(client);
        return "redirect:/clients";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable long id, Model model) {
        model.addAttribute("client", clientService.get(id));
        model.addAttribute("page", "clients");
        return "clients/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable long id, @ModelAttribute Client client) {
        client.setId(id);
        clientService.save(client);
        return "redirect:/clients";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable long id) {
        clientService.delete(id);
        return "redirect:/clients";
    }
}

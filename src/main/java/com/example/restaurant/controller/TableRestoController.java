package com.example.restaurant.controller;

import com.example.restaurant.service.TableRestoService;
import com.example.restaurant.model.TableResto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tables")
public class TableRestoController {
    private final TableRestoService tableRestoService;

    public TableRestoController(TableRestoService tableRestoService) {
        this.tableRestoService = tableRestoService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("tables", tableRestoService.listAll());
        model.addAttribute("totalSeats", tableRestoService.totalSeats());
        model.addAttribute("page", "tables");
        return "tables/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("tableResto", new TableResto());
        model.addAttribute("page", "tables");
        return "tables/form";
    }

    @PostMapping
    public String create(@ModelAttribute TableResto tableResto) {
        tableRestoService.save(tableResto);
        return "redirect:/tables";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable long id, Model model) {
        model.addAttribute("tableResto", tableRestoService.get(id));
        model.addAttribute("page", "tables");
        return "tables/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable long id, @ModelAttribute TableResto tableResto) {
        tableResto.setId(id);
        tableRestoService.save(tableResto);
        return "redirect:/tables";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable long id) {
        tableRestoService.delete(id);
        return "redirect:/tables";
    }
}

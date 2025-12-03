package org.iesalixar.daw2.cine.controllers;


import org.iesalixar.daw2.cine.entities.Pelicula;
import org.iesalixar.daw2.cine.entities.Sala;
import org.iesalixar.daw2.cine.entities.Funcion;
import org.iesalixar.daw2.cine.repositories.SalaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/salas")
public class SalaController {
    private static final Logger logger = LoggerFactory.getLogger(SalaController.class);

    @Autowired
    private SalaRepository salaRepository;

    @GetMapping()
    public String listSalas(@RequestParam(defaultValue = "1") int page,
                            @RequestParam(required = false) String search,
                            @RequestParam(required = false) String sort,
                            Model model) {
        logger.info("Solicitando listado de todas las salas..." + search);
        Pageable pageable = PageRequest.of(page - 1, 5, getSort(sort));
        Page<Sala> salas;
        int totalPages = 0;

        if (search != null && !search.isBlank()) {
            try {
                int numeroSala = Integer.parseInt(search);
                salas = salaRepository.findByNumero(numeroSala, pageable);
                totalPages = (int) Math.ceil((double) salaRepository.countByNumero(numeroSala) / 5);
            } catch (NumberFormatException e) {
                // Si el usuario ingresa texto en lugar de un número, mostrar lista vacía
                salas = Page.empty(pageable);
                totalPages = 0;
            }
        } else {
            salas = salaRepository.findAll(pageable);
            totalPages = (int) Math.ceil((double) salaRepository.count() / 5);
        }

        logger.info("Se han cargado {} salas.", salas.toList().size());
        model.addAttribute("listSalas", salas.toList());
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        return "salas";
    }

    @GetMapping("/new")
    public String showNewForm(org.springframework.ui.Model model, RedirectAttributes redirectAttributes) {
        model.addAttribute("sala", new Sala());
        try {
            List<Sala> listSalas = salaRepository.findAll();
            model.addAttribute("salas", listSalas);
        } catch (Exception e) {
            e.printStackTrace(); // imprime la causa exacta del error 500
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar salas.");
            return "redirect:/salas";
        }
        return "sala-form";
    }
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, org.springframework.ui.Model model, RedirectAttributes redirectAttributes) {
        try {
            if (salaRepository == null) {
                throw new IllegalStateException("salaDAO no inyectado");
            }
            Optional<Sala> sala = salaRepository.findById(id);
            if (sala == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Sala no encontrada.");
                return "redirect:/salas";
            }
            model.addAttribute("sala", sala);
        } catch (Exception e) {
            logger.error("Error inesperado: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error interno del servidor.");
            return "redirect:/salas";
        }
        return "sala-form";
    }
    @PostMapping("/delete")
    public String deleteSala(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            if (salaRepository == null) {
                throw new IllegalStateException("salaDAO no inyectado");
            }
            Optional<Sala> sala = salaRepository.findById(id);
            if (sala == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Sala no encontrada.");
                return "redirect:/salas";
            }
            salaRepository.deleteById(id);
            logger.info("Sala con ID {} eliminada con éxito.", id);
        } catch (Exception e) {
            logger.error("Error inesperado al eliminar la sala: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error interno del servidor.");
        }
        return "redirect:/salas";
    }
    private Sort getSort(String sort) {
        if (sort == null) {
            return Sort.by("id").ascending();
        }
        return switch (sort) {
            case "nameAsc" -> Sort.by("name").ascending();
            case "nameDesc" -> Sort.by("name").descending();
            case "idDesc" -> Sort.by("id").descending();
            default -> Sort.by("id").ascending();
        };
    }
}

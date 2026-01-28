package org.iesalixar.daw2.cine.controllers;

import org.iesalixar.daw2.cine.entities.Sala;
import org.iesalixar.daw2.cine.entities.Funcion;
import org.iesalixar.daw2.cine.repositories.FuncionRepository;
import org.iesalixar.daw2.cine.repositories.SalaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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

    @Autowired
    private FuncionRepository funcionRepository;

    /**
     * LISTAR SALAS
     * Permitido para: USER, MANAGER, ADMIN
     */
    @GetMapping()
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    public String listSalas(@RequestParam(defaultValue = "1") int page,
                            @RequestParam(required = false) String search,
                            @RequestParam(required = false) String sort,
                            Model model) {
        logger.info("Solicitando listado de salas. Búsqueda: {}", search);
        Pageable pageable = PageRequest.of(page - 1, 5, getSort(sort));
        Page<Sala> salas;

        if (search != null && !search.isBlank()) {
            // Asumiendo que el repositorio maneja la búsqueda por número de sala
            salas = salaRepository.findByNumeroContainingIgnoreCase(search, pageable);
        } else {
            salas = salaRepository.findAll(pageable);
        }

        model.addAttribute("listSalas", salas.getContent());
        model.addAttribute("totalPages", salas.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        return "Sala/salas.html";
    }

    /**
     * FORMULARIO NUEVA SALA
     * Permitido para: MANAGER, ADMIN
     */
    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public String showNewForm(Model model, RedirectAttributes redirectAttributes) {
        if (!model.containsAttribute("sala")) {
            model.addAttribute("sala", new Sala());
        }
        model.addAttribute("funciones", funcionRepository.findAll());
        return "Sala/salas-form.html";
    }

    /**
     * INSERTAR SALA
     * Permitido para: MANAGER, ADMIN
     */
    @PostMapping("/insert")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public String insertSala(@ModelAttribute("sala") Sala sala,
                             @RequestParam(value = "funciones", required = false) List<Long> funcionesIds,
                             RedirectAttributes redirectAttributes) {
        try {
            if (funcionesIds != null) {
                List<Funcion> funcionesSeleccionadas = funcionRepository.findAllById(funcionesIds);
                for (Funcion f : funcionesSeleccionadas) {
                    f.setSala(sala);
                }
                sala.setFunciones(funcionesSeleccionadas);
            }
            salaRepository.save(sala);
            redirectAttributes.addFlashAttribute("successMessage", "Sala creada correctamente.");
        } catch (Exception e) {
            logger.error("Error al crear sala: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al crear la sala.");
            redirectAttributes.addFlashAttribute("sala", sala);
            return "redirect:/salas/new";
        }
        return "redirect:/salas";
    }

    /**
     * FORMULARIO EDITAR
     * Permitido para: MANAGER, ADMIN
     */
    @GetMapping("/edit")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public String showEditForm(@RequestParam("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Sala sala = salaRepository.findById(id).orElse(null);
        if (sala == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sala no encontrada.");
            return "redirect:/salas";
        }

        model.addAttribute("sala", sala);
        model.addAttribute("funciones", funcionRepository.findAll());
        return "Sala/salas-form.html";
    }

    /**
     * ACTUALIZAR SALA
     * Permitido para: MANAGER, ADMIN
     */
    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public String updateSala(@ModelAttribute("sala") Sala sala,
                             @RequestParam(value = "funciones", required = false) List<Long> funcionesIds,
                             RedirectAttributes redirectAttributes) {
        try {
            Sala existingSala = salaRepository.findById(sala.getId())
                    .orElseThrow(() -> new RuntimeException("Sala no encontrada"));

            existingSala.setNumero(sala.getNumero());
            existingSala.setCapacidad(sala.getCapacidad());

            if (funcionesIds != null) {
                List<Funcion> funcionesSeleccionadas = funcionRepository.findAllById(funcionesIds);
                for (Funcion f : funcionesSeleccionadas) {
                    f.setSala(existingSala);
                }
                existingSala.setFunciones(funcionesSeleccionadas);
            }

            salaRepository.save(existingSala);
            redirectAttributes.addFlashAttribute("successMessage", "Sala actualizada correctamente.");
        } catch (Exception e) {
            logger.error("Error al actualizar sala: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar la sala.");
            return "redirect:/salas/edit?id=" + sala.getId();
        }
        return "redirect:/salas";
    }

    /**
     * ELIMINAR SALA
     * Permitido para: MANAGER, ADMIN
     */
    @PostMapping("/delete")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public String deleteSala(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            salaRepository.deleteById(id);
            logger.info("Sala con ID {} eliminada.", id);
            redirectAttributes.addFlashAttribute("successMessage", "Sala eliminada con éxito.");
        } catch (Exception e) {
            logger.error("Error al eliminar la sala: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "No se puede eliminar la sala (posiblemente tiene funciones asignadas).");
        }
        return "redirect:/salas";
    }

    private Sort getSort(String sort) {
        if (sort == null) return Sort.by("id").ascending();
        return switch (sort) {
            case "numAsc" -> Sort.by("numero").ascending();
            case "numDesc" -> Sort.by("numero").descending();
            case "capDesc" -> Sort.by("capacidad").descending();
            default -> Sort.by("id").ascending();
        };
    }
}
package org.iesalixar.daw2.cine.controllers;

import jakarta.validation.Valid;
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
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/salas")
public class SalaController {
    private static final Logger logger = LoggerFactory.getLogger(SalaController.class);

    @Autowired
    private SalaRepository salaRepository;

    @Autowired
    private FuncionRepository funcionRepository;


    @GetMapping()
    public String listSalas(@RequestParam(defaultValue = "1") int page, @RequestParam(required = false) String search, @RequestParam(required = false) String sort, Model model) {
        logger.info("Solicitando listado de todas las salas..." + search);
        Pageable pageable = PageRequest.of(page - 1, 5, getSort(sort));
        Page<Sala> salas;
        int totalPages = 0;
        if (search != null && !search.isBlank()) {
            salas = salaRepository.findByNumeroContainingIgnoreCase(search, pageable);
            totalPages = (int) Math.ceil((double) salaRepository.countByNumeroContainingIgnoreCase(search) / 5);
        } else {
            salas = salaRepository.findAll(pageable);
            totalPages = (int) Math.ceil((double) salaRepository.count() / 5);
        }
        logger.info("Se han cargado {} peliculas.", salas.toList().size());
        model.addAttribute("listSalas", salas.toList()); // Pasar la lista de salas al modelo
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        return "salas"; // Nombre de la plantilla Thymeleaf a renderizar
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        model.addAttribute("sala", new Sala());
        List<Funcion> funciones = funcionRepository.findAll(); // trae todas las funciones
        model.addAttribute("funciones", funciones);
        return "salas-form";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Sala> salaOpt = salaRepository.findById(id);
        if (salaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sala no encontrada.");
            return "redirect:/salas";
        }

        model.addAttribute("sala", salaOpt.get());
        List<Funcion> funciones = funcionRepository.findAll();
        model.addAttribute("funciones", funciones);

        return "salas-form";
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

    @PostMapping("/insert")
    public String insertSala(@ModelAttribute("sala") Sala sala,
                             @RequestParam("funciones") List<Long> funcionesIds,
                             RedirectAttributes redirectAttributes) {
        try {
            List<Funcion> funcionesSeleccionadas = funcionRepository.findAllById(funcionesIds);

            // Asignar la sala a cada funcion antes de guardar
            for (Funcion f : funcionesSeleccionadas) {
                f.setSala(sala);
            }

            sala.setFunciones(funcionesSeleccionadas);
            salaRepository.save(sala);

            redirectAttributes.addFlashAttribute("successMessage", "Sala creada correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error al crear la sala.");
        }

        return "redirect:/salas";
    }

    @PostMapping("/update")
    public String updateSala(@ModelAttribute("sala") Sala sala,
                             @RequestParam("funciones") List<Long> funcionesIds,
                             RedirectAttributes redirectAttributes) {
        try {
            Sala existingSala = salaRepository.findById(sala.getId())
                    .orElseThrow(() -> new RuntimeException("Sala no encontrada"));

            existingSala.setNumero(sala.getNumero());
            existingSala.setCapacidad(sala.getCapacidad());

            // Actualizar funciones seleccionadas
            List<Funcion> funcionesSeleccionadas = funcionRepository.findAllById(funcionesIds);

            // Asignar la sala a cada funcion
            for (Funcion f : funcionesSeleccionadas) {
                f.setSala(existingSala);
            }

            existingSala.setFunciones(funcionesSeleccionadas);
            salaRepository.save(existingSala);

            redirectAttributes.addFlashAttribute("successMessage", "Sala actualizada correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar la sala.");
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
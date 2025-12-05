package org.iesalixar.daw2.cine.controllers;

import org.iesalixar.daw2.cine.entities.Funcion;
import org.iesalixar.daw2.cine.entities.Pelicula;
import org.iesalixar.daw2.cine.entities.Sala;
import org.iesalixar.daw2.cine.repositories.FuncionRepository;
import org.iesalixar.daw2.cine.repositories.SalaRepository;
import org.iesalixar.daw2.cine.repositories.PeliculaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/funciones")
public class FuncionController {

    private static final Logger logger = LoggerFactory.getLogger(FuncionController.class);

    @Autowired
    private FuncionRepository funcionRepository;
    @Autowired
    private SalaRepository salaRepository;

    @Autowired
    private PeliculaRepository peliculaRepository;

    /* =========================
       LISTAR Y MOSTRAR FORMULARIOS (GET)
       ========================= */
    @GetMapping
    public String listFunciones(@RequestParam(defaultValue = "1") int page, @RequestParam(required = false) String search, @RequestParam(required = false) String sort, Model model) {
        Pageable pageable = PageRequest.of(page - 1, 5, getSort(sort));
        Page<Funcion> funcionesPage;
        if (search != null && !search.isBlank()) {
            funcionesPage = funcionRepository.findByCodeContainingIgnoreCase(search, pageable);
        } else {
            funcionesPage = funcionRepository.findAll(pageable);
        }
        model.addAttribute("funciones", funcionesPage.getContent());
        model.addAttribute("totalPages", funcionesPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);

        logger.info("Funciones cargadas: {}", funcionesPage.getContent().size());

        return "funciones";
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        model.addAttribute("funcion", new Funcion());
        model.addAttribute("salas", salaRepository.findAll());
        model.addAttribute("peliculas", peliculaRepository.findAll());
        return "funcion-form";
    }

    // ... (MÉTODOS showEditForm, insert, update, delete permanecen sin cambios) ...

    @GetMapping("/edit")
    public String showEditForm(
            @RequestParam("id") Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        Optional<Funcion> optFuncion = funcionRepository.findById(id);

        if (optFuncion.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Función no encontrada.");
            return "redirect:/funciones";
        }

        model.addAttribute("funcion", optFuncion.get());
        model.addAttribute("salas", salaRepository.findAll());
        model.addAttribute("peliculas", peliculaRepository.findAll());

        return "funcion-form";
    }

    @PostMapping("/insert")
    public String insertFuncion(
            @ModelAttribute Funcion funcion,
            @RequestParam("sala") Long salaId,
            @RequestParam("pelicula") Long peliculaId,
            Model model,
            RedirectAttributes redirectAttributes) {

        return saveOrUpdateFuncion(funcion, salaId, peliculaId, model, redirectAttributes, true);
    }

    @PostMapping("/update")
    public String updateFuncion(
            @ModelAttribute Funcion funcion,
            @RequestParam("sala") Long salaId,
            @RequestParam("pelicula") Long peliculaId,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (funcion.getId() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: Intento de actualizar una función sin ID válido.");
            return "redirect:/funciones";
        }

        return saveOrUpdateFuncion(funcion, salaId, peliculaId, model, redirectAttributes, false);
    }

    private String saveOrUpdateFuncion(
            Funcion funcion,
            Long salaId,
            Long peliculaId,
            Model model,
            RedirectAttributes redirectAttributes,
            boolean isInsert) {

        String action = isInsert ? "crear" : "actualizar";

        try {
            if (!isInsert && !funcionRepository.existsById(funcion.getId())) {
                throw new RuntimeException("Función no encontrada para actualizar.");
            }

            Optional<Funcion> existingCode;
            if (isInsert) {
                existingCode = funcionRepository.findByCode(funcion.getCode());
            } else {
                existingCode = funcionRepository.findByCodeAndIdNot(funcion.getCode(), funcion.getId());
            }

            if (existingCode.isPresent()) {
                throw new RuntimeException("El código de función '" + funcion.getCode() + "' ya está siendo usado por otra función.");
            }

            Sala sala = salaRepository.findById(salaId).orElseThrow(
                    () -> new RuntimeException("Sala no encontrada."));
            Pelicula pelicula = peliculaRepository.findById(peliculaId).orElseThrow(
                    () -> new RuntimeException("Película no encontrada."));

            funcion.setSala(sala);
            funcion.setPelicula(pelicula);

            funcionRepository.save(funcion);

            redirectAttributes.addFlashAttribute("successMessage", "Función " + action + " correctamente.");
            return "redirect:/funciones";

        } catch (Exception e) {
            logger.error("Error al {} la función:", action, e);

            model.addAttribute("errorMessage", "Error al " + action + " la función: " + e.getMessage());

            model.addAttribute("funcion", funcion);
            model.addAttribute("salas", salaRepository.findAll());
            model.addAttribute("peliculas", peliculaRepository.findAll());

            return "funcion-form";
        }
    }

    @PostMapping("/delete")
    public String deleteFuncion(
            @RequestParam("id") Long id,
            RedirectAttributes redirectAttributes) {

        Optional<Funcion> funcion = funcionRepository.findById(id);

        if (funcion.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Función no encontrada.");
            return "redirect:/funciones";
        }

        try {
            funcionRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Función eliminada correctamente.");
        } catch (Exception e) {
            logger.error("Error al eliminar la función", e);
            redirectAttributes.addFlashAttribute("errorMessage", "No se pudo eliminar la función. (Posiblemente tenga boletos asociados)");
        }

        return "redirect:/funciones";
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
package org.iesalixar.daw2.cine.controllers;

import org.iesalixar.daw2.cine.entities.Funcion;
import org.iesalixar.daw2.cine.entities.Pelicula;
import org.iesalixar.daw2.cine.entities.Sala;
import org.iesalixar.daw2.cine.repositories.FuncionRepository;
import org.iesalixar.daw2.cine.repositories.PeliculaRepository;
import org.iesalixar.daw2.cine.repositories.SalaRepository;
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

    @GetMapping
    public String listFunciones(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort,
            Model model) {

        Pageable pageable = PageRequest.of(page - 1, 5, getSort(sort));
        Page<Funcion> funciones;

        if (search != null && !search.isBlank()) {
            funciones = funcionRepository.findByCodeContainingIgnoreCase(search, pageable);
        } else {
            funciones = funcionRepository.findAll(pageable);
        }

        model.addAttribute("funciones", funciones.getContent());
        model.addAttribute("totalPages", funciones.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);

        logger.info("Funciones cargadas: {}", funciones.getContent().size());

        return "funciones";
    }
    @GetMapping("/new")
    public String showNewForm(Model model) {
        model.addAttribute("funcion", new Funcion());

        // 💡 SOLUCIÓN: Carga las listas de salas y películas
        model.addAttribute("salas", salaRepository.findAll());
        model.addAttribute("peliculas", peliculaRepository.findAll());

        return "funcion-form";
    }
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

        // 💡 SOLUCIÓN: Carga las listas de salas y películas
        model.addAttribute("salas", salaRepository.findAll());
        model.addAttribute("peliculas", peliculaRepository.findAll());

        return "funcion-form";
    }
    @PostMapping("/insert")
    public String insertFuncion(
            @ModelAttribute Funcion funcion,
            @RequestParam("sala") Long salaId,
            @RequestParam("pelicula") Long peliculaId,
            Model model, // 💡 Añadimos Model para poder recargar la vista con el error
            RedirectAttributes redirectAttributes) {

        try {
            // 1. VALIDACIÓN: Verificar si el código ya existe
            if (funcionRepository.findByCode(funcion.getCode()).isPresent()) {
                model.addAttribute("errorMessage", "El código de función '" + funcion.getCode() + "' ya existe.");
                // Recargar listas necesarias para la vista
                model.addAttribute("salas", salaRepository.findAll());
                model.addAttribute("peliculas", peliculaRepository.findAll());
                return "funcion-form";
            }

            // 2. Cargar entidades completas (Solución al problema anterior)
            Sala sala = salaRepository.findById(salaId).orElseThrow(
                    () -> new RuntimeException("Sala no encontrada."));
            Pelicula pelicula = peliculaRepository.findById(peliculaId).orElseThrow(
                    () -> new RuntimeException("Película no encontrada."));

            // 3. Asignar y guardar
            funcion.setSala(sala);
            funcion.setPelicula(pelicula);
            funcionRepository.save(funcion);

            redirectAttributes.addFlashAttribute("successMessage", "Función creada correctamente.");
        } catch (Exception e) {
            logger.error("Error al insertar la función", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error al crear la función: " + e.getMessage());
        }

        return "redirect:/funciones";
    }
    @PostMapping("/update")
    public String updateFuncion(
            @ModelAttribute Funcion funcion,
            @RequestParam("sala") Long salaId,
            @RequestParam("pelicula") Long peliculaId,
            Model model, // 💡 Añadimos Model para poder recargar la vista con el error
            RedirectAttributes redirectAttributes) {

        // Verificación de ID
        if (funcion.getId() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error de ruta: Intento de actualizar una función sin ID válido.");
            return "redirect:/funciones";
        }

        try {
            // 1. VALIDACIÓN: Verificar si el código ya existe en otra función
            if (funcionRepository.findByCodeAndIdNot(funcion.getCode(), funcion.getId()).isPresent()) {
                model.addAttribute("errorMessage", "El código de función '" + funcion.getCode() + "' ya está siendo usado por otra función.");
                // Recargar listas necesarias para la vista
                model.addAttribute("salas", salaRepository.findAll());
                model.addAttribute("peliculas", peliculaRepository.findAll());
                return "funcion-form";
            }

            // 2. Cargar entidades completas (Necesario para el UPDATE)
            Sala sala = salaRepository.findById(salaId).orElseThrow(
                    () -> new RuntimeException("Sala no encontrada."));
            Pelicula pelicula = peliculaRepository.findById(peliculaId).orElseThrow(
                    () -> new RuntimeException("Película no encontrada."));

            // 3. Asignar y guardar
            funcion.setSala(sala);
            funcion.setPelicula(pelicula);
            funcionRepository.save(funcion); // Realiza la actualización

            redirectAttributes.addFlashAttribute("successMessage", "Función actualizada correctamente.");
        } catch (Exception e) {
            logger.error("Error al actualizar la función", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar la función: " + e.getMessage());
        }

        return "redirect:/funciones";
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
            redirectAttributes.addFlashAttribute("errorMessage", "No se pudo eliminar la función.");
        }

        return "redirect:/funciones";
    }
    private Sort getSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by("id").ascending();
        }

        return switch (sort) {
            case "codeAsc" -> Sort.by("code").ascending();
            case "codeDesc" -> Sort.by("code").descending();
            case "idDesc" -> Sort.by("id").descending();
            default -> Sort.by("id").ascending();
        };
    }
}

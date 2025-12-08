package org.iesalixar.daw2.cine.controllers;

import org.iesalixar.daw2.cine.entities.Funcion;
import org.iesalixar.daw2.cine.entities.Pelicula;
import org.iesalixar.daw2.cine.entities.Sala;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    /** Logger para registrar eventos y errores */
    private static final Logger logger = LoggerFactory.getLogger(FuncionController.class);

    /** Repositorio para operaciones CRUD de funciones */
    @Autowired
    private FuncionRepository funcionRepository;
    
    /** Repositorio para operaciones CRUD de salas */
    @Autowired
    private SalaRepository salaRepository;

    /** Repositorio para operaciones CRUD de películas */
    @Autowired
    private PeliculaRepository peliculaRepository;

    @GetMapping
    public String listFunciones(@RequestParam(defaultValue = "1") int page, @RequestParam(required = false) String search, @RequestParam(required = false) String sort, Model model) {
        Pageable pageable = PageRequest.of(page - 1, 5, getSort(sort));
        Page<Funcion> funcionesPage;

        if (search != null && !search.isBlank()) {
            // Busca por el título de la película (ahora asumimos que search es el título)
            funcionesPage = funcionRepository.findByPeliculaTituloContainingIgnoreCase(search, pageable);
        } else {
            funcionesPage = funcionRepository.findAll(pageable);
        }

        model.addAttribute("funciones", funcionesPage.getContent());
        model.addAttribute("totalPages", funcionesPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);

        logger.info("Funciones cargadas: {}", funcionesPage.getContent().size());

        return "Funcion/funciones.html";
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        model.addAttribute("funcion", new Funcion());
        model.addAttribute("salas", salaRepository.findAll());
        model.addAttribute("peliculas", peliculaRepository.findAll());
        return "Funcion/funcion-form.html";
    }

    @GetMapping("/details")
    public String showDetails(
            @RequestParam("id") Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        Optional<Funcion> optFuncion = funcionRepository.findById(id);

        if (optFuncion.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Función no encontrada.");
            return "redirect:/funciones";
        }

        Funcion funcion = optFuncion.get();
        model.addAttribute("funcion", funcion);

        return "Funcion/funcion-details.html";
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
        model.addAttribute("salas", salaRepository.findAll());
        model.addAttribute("peliculas", peliculaRepository.findAll());

        return "Funcion/funcion-form.html";
    }

    @PostMapping("/insert")
    public String insertFuncion(
            @ModelAttribute Funcion funcion,
            @RequestParam("sala") Long salaId,
            @RequestParam("pelicula") Long peliculaId,
            Model model,
            RedirectAttributes redirectAttributes) {

        String action = "crear";

        try {
            // aqui validamos el código para la inserción
            Optional<Funcion> existingCode = funcionRepository.findByCode(funcion.getCode());

            if (existingCode.isPresent()) {
                throw new RuntimeException("El código de función '" + funcion.getCode() + "' ya está siendo usado por otra función.");
            }

            // obtener y asignar entidades
            Sala sala = salaRepository.findById(salaId).orElseThrow(
                    () -> new RuntimeException("Sala no encontrada."));
            Pelicula pelicula = peliculaRepository.findById(peliculaId).orElseThrow(
                    () -> new RuntimeException("Película no encontrada."));

            funcion.setSala(sala);
            funcion.setPelicula(pelicula);

            // guardar la nueva función
            funcionRepository.save(funcion);

            redirectAttributes.addFlashAttribute("successMessage", "Función creada correctamente.");
            return "redirect:/funciones";

        } catch (Exception e) {
            logger.error("Error al {} la función:", action, e);

            model.addAttribute("errorMessage", "Error al " + action + " la función: " + e.getMessage());

            // recargar datos para el formulario
            model.addAttribute("funcion", funcion);
            model.addAttribute("salas", salaRepository.findAll());
            model.addAttribute("peliculas", peliculaRepository.findAll());

            return "Funcion/funcion-form.html";
        }
    }

    @PostMapping("/update")
    public String updateFuncion(
            @ModelAttribute Funcion funcion,
            @RequestParam("sala") Long salaId,
            @RequestParam("pelicula") Long peliculaId,
            Model model,
            RedirectAttributes redirectAttributes) {

        String action = "actualizar";

        try {
            // validación de ID y de su existencia
            if (funcion.getId() == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Error: Intento de actualizar una función sin ID válido.");
                return "redirect:/funciones";
            }
            if (!funcionRepository.existsById(funcion.getId())) {
                throw new RuntimeException("Función no encontrada para actualizar.");
            }

            // validación de código para actualización, tiene que ser único
            Optional<Funcion> existingCode = funcionRepository.findByCodeAndIdNot(funcion.getCode(), funcion.getId());

            if (existingCode.isPresent()) {
                throw new RuntimeException("El código de función '" + funcion.getCode() + "' ya está siendo usado por otra función.");
            }

            // obtener y asignar entidades
            Sala sala = salaRepository.findById(salaId).orElseThrow(
                    () -> new RuntimeException("Sala no encontrada."));
            Pelicula pelicula = peliculaRepository.findById(peliculaId).orElseThrow(
                    () -> new RuntimeException("Película no encontrada."));

            funcion.setSala(sala);
            funcion.setPelicula(pelicula);

            // actualizar la función
            funcionRepository.save(funcion);

            redirectAttributes.addFlashAttribute("successMessage", "Función actualizada correctamente.");
            return "redirect:/funciones";

        } catch (Exception e) {
            logger.error("Error al {} la función:", action, e);

            model.addAttribute("errorMessage", "Error al " + action + " la función: " + e.getMessage());

            // recargar datos para el formulario
            model.addAttribute("funcion", funcion);
            model.addAttribute("salas", salaRepository.findAll());
            model.addAttribute("peliculas", peliculaRepository.findAll());

            return "Funcion/funcion-form.html";
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
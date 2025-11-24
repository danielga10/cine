package org.iesalixar.daw2.cine.controllers;

import org.iesalixar.daw2.cine.entities.Pelicula;
import org.iesalixar.daw2.cine.repositories.PeliculaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/peliculas")
public class PeliculaController {

    private static final Logger logger = LoggerFactory.getLogger(PeliculaController.class);

    @Autowired
    private PeliculaRepository peliculaRepository;

    /** Lista todas las peliculas */
    @GetMapping()
    public String listPeliculas(@RequestParam(defaultValue = "1") int page, @RequestParam(required = false) String search, @RequestParam(required = false) String sort, Model model) {
        logger.info("Solicitando la lista de todas las peliculas..." + search);
        Pageable pageable = PageRequest.of(page - 1, 5, getSort(sort));
        Page<Pelicula> peliculas;
        int totalPages = 0;
        if (search != null && !search.isBlank()) {
            peliculas = peliculaRepository.findByTituloContainingIgnoreCase(search, pageable);
            totalPages = (int) Math.ceil((double) peliculaRepository.countByTituloContainingIgnoreCase(search) / 5);
        } else {
            peliculas = peliculaRepository.findAll(pageable);
            totalPages = (int) Math.ceil((double) peliculaRepository.count() / 5);
        }
        logger.info("Se han cargado {} peliculas.", peliculas.toList().size());
        model.addAttribute("listPeliculas", peliculas.toList()); // Pasar la lista de peliculas al modelo
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        return "pelicula"; // Nombre de la plantilla Thymeleaf a renderizar
    }


    /** Formulario para crear una nueva pelicula */
    @GetMapping("/new")
    public String showNewForm(Model model, RedirectAttributes redirectAttributes) {
        model.addAttribute("pelicula", new Pelicula());
        try {
            List<Pelicula> listPeliculas = peliculaRepository.findAll();
            model.addAttribute("peliculas", listPeliculas);
        } catch (Exception e) {
            e.printStackTrace(); // imprime la causa exacta del error 500
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar peliculas.");
            return "redirect:/peliculas";
        }
        return "pelicula-form";
    }

    /** Formulario para editar una pelicula existente */
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            if (peliculaRepository == null) {
                throw new IllegalStateException("peliculaDAO no inyectado");
            }
            Optional<Pelicula> pelicula = peliculaRepository.findById(id);
            if (pelicula == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Pelicula no encontrada.");
                return "redirect:/peliculas";
            }
            model.addAttribute("pelicula", pelicula);
        } catch (Exception e) {
            logger.error("Error inesperado: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error interno del servidor.");
            return "redirect:/peliculas";
        }
        return "pelicula-form";
    }

    /** Elimina una pelicula */
    @PostMapping("/delete")
    public String deletePelicula(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            if (peliculaRepository == null) {
                throw new IllegalStateException("peliculaDAO no inyectado");
            }
            Optional<Pelicula> pelicula = peliculaRepository.findById(id);
            if (pelicula == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Pelicula no encontrada.");
                return "redirect:/peliculas";
            }
            peliculaRepository.deleteById(id);
            logger.info("Pelicula con ID {} eliminada con éxito.", id);
        } catch (Exception e) {
            logger.error("Error inesperado al eliminar la pelicula: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error interno del servidor.");
        }
        return "redirect:/peliculas";
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
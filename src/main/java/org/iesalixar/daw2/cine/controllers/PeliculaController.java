package org.iesalixar.daw2.cine.controllers;

import org.iesalixar.daw2.cine.entities.Pelicula;
import org.iesalixar.daw2.cine.entities.Director;
import org.iesalixar.daw2.cine.repositories.DirectorRepository;
import org.iesalixar.daw2.cine.repositories.PeliculaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/peliculas")
public class PeliculaController {

    private static final Logger logger = LoggerFactory.getLogger(PeliculaController.class);

    @Autowired
    private PeliculaRepository peliculaRepository;

    @Autowired
    private DirectorRepository directorRepository;

    /** * LISTAR PELÍCULAS
     * Permitido para: USER, MANAGER, ADMIN
     */
    @GetMapping()
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    public String listPeliculas(@RequestParam(defaultValue = "1") int page,
                                @RequestParam(required = false) String search,
                                @RequestParam(required = false) String sort,
                                Model model) {
        logger.info("Solicitando la lista de peliculas. Búsqueda: {}", search);

        Pageable pageable = PageRequest.of(page - 1, 5, getSort(sort));
        Page<Pelicula> peliculas;

        if (search != null && !search.isBlank()) {
            peliculas = peliculaRepository.findByTituloContainingIgnoreCase(search, pageable);
        } else {
            peliculas = peliculaRepository.findAll(pageable);
        }

        model.addAttribute("listPeliculas", peliculas.getContent());
        model.addAttribute("totalPages", peliculas.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);

        return "Pelicula/pelicula.html";
    }

    /** * FORMULARIO NUEVA PELÍCULA
     * Permitido para: MANAGER, ADMIN
     */
    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public String showNewForm(Model model) {
        if (!model.containsAttribute("pelicula")) {
            model.addAttribute("pelicula", new Pelicula());
        }
        model.addAttribute("directores", directorRepository.findAll());
        return "Pelicula/pelicula-form.html";
    }

    /** * INSERTAR PELÍCULA
     * Permitido para: MANAGER, ADMIN
     */
    @PostMapping("/insert")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public String insertPelicula(@ModelAttribute Pelicula pelicula, RedirectAttributes redirectAttrs) {
        try {
            if (pelicula.getDirector() == null || pelicula.getDirector().getId() == null) {
                throw new IllegalArgumentException("Debe seleccionar un Director.");
            }
            peliculaRepository.save(pelicula);
            redirectAttrs.addFlashAttribute("successMessage", "Película creada correctamente.");
        } catch (Exception e) {
            logger.error("Error al crear la película: {}", e.getMessage());
            redirectAttrs.addFlashAttribute("pelicula", pelicula);
            redirectAttrs.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/peliculas/new";
        }
        return "redirect:/peliculas";
    }

    /** * FORMULARIO EDITAR
     * Permitido para: MANAGER, ADMIN
     */
    @GetMapping("/edit")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public String showEditForm(@RequestParam("id") Long id, Model model) {
        Pelicula pelicula = peliculaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de película no válido: " + id));

        model.addAttribute("pelicula", pelicula);
        model.addAttribute("directores", directorRepository.findAll());
        return "Pelicula/pelicula-form.html";
    }

    /** * ACTUALIZAR PELÍCULA
     * Permitido para: MANAGER, ADMIN
     */
    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public String updatePelicula(@ModelAttribute Pelicula pelicula, RedirectAttributes redirectAttrs) {
        try {
            peliculaRepository.save(pelicula);
            redirectAttrs.addFlashAttribute("successMessage", "Película actualizada correctamente.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMessage", "Error al actualizar: " + e.getMessage());
            return "redirect:/peliculas/edit?id=" + pelicula.getId();
        }
        return "redirect:/peliculas";
    }

    /** * ELIMINAR PELÍCULA
     * Permitido para: MANAGER, ADMIN
     */
    @PostMapping("/delete")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public String deletePelicula(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            peliculaRepository.deleteById(id);
            logger.info("Pelicula con ID {} eliminada.", id);
            redirectAttributes.addFlashAttribute("successMessage", "Película eliminada.");
        } catch (Exception e) {
            logger.error("Error al eliminar: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "No se pudo eliminar la película.");
        }
        return "redirect:/peliculas";
    }

    private Sort getSort(String sort) {
        if (sort == null) return Sort.by("id").ascending();
        return switch (sort) {
            case "nameAsc" -> Sort.by("titulo").ascending();
            case "nameDesc" -> Sort.by("titulo").descending();
            case "idDesc" -> Sort.by("id").descending();
            default -> Sort.by("id").ascending();
        };
    }
}
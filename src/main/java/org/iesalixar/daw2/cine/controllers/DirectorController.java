package org.iesalixar.daw2.cine.controllers;

import org.iesalixar.daw2.cine.entities.Director;
import org.iesalixar.daw2.cine.entities.Pelicula;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/directores")
public class DirectorController {

    private static final Logger logger = LoggerFactory.getLogger(DirectorController.class);

    @Autowired
    private DirectorRepository directorRepository;

    @Autowired
    private PeliculaRepository peliculaRepository;

    /**
     * LISTAR DIRECTORES
     * Permitido para: USER, MANAGER, ADMIN
     */
    @GetMapping()
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    public String listDirectores(@RequestParam(defaultValue = "1") int page,
                                 @RequestParam(required = false) String search,
                                 @RequestParam(required = false) String sort,
                                 Model model ) {
        logger.info("Solicitando listado de directores. Búsqueda: {}", search);
        Pageable pageable = PageRequest.of(page - 1, 5, getSort(sort));
        Page<Director> directores;

        if (search != null && !search.isBlank()) {
            directores = directorRepository.findByNombreContainingIgnoreCase(search, pageable);
        } else {
            directores = directorRepository.findAll(pageable);
        }

        model.addAttribute("listDirectores", directores.getContent());
        model.addAttribute("totalPages", directores.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        return "Director/directores.html";
    }

    /**
     * FORMULARIO NUEVO DIRECTOR
     * Permitido para: MANAGER, ADMIN
     */
    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public String showNewForm(Model model) {
        if (!model.containsAttribute("director")) {
            model.addAttribute("director", new Director());
        }
        model.addAttribute("peliculas", peliculaRepository.findAll());
        return "Director/directores-form.html";
    }

    /**
     * INSERTAR DIRECTOR
     * Permitido para: MANAGER, ADMIN
     */
    @PostMapping("/insert")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public String insertDirector(@ModelAttribute("director") Director director,
                                 @RequestParam(value = "peliculas", required = false) List<Long> peliculasIds,
                                 RedirectAttributes redirectAttributes) {
        try {
            if (peliculasIds != null) {
                List<Pelicula> peliculasSeleccionadas = peliculaRepository.findAllById(peliculasIds);
                for (Pelicula p : peliculasSeleccionadas) {
                    p.setDirector(director);
                }
                director.setPeliculas(peliculasSeleccionadas);
            }
            directorRepository.save(director);
            redirectAttributes.addFlashAttribute("successMessage", "Director creado correctamente.");
        } catch (Exception e) {
            logger.error("Error al crear director: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al crear el director.");
        }
        return "redirect:/directores";
    }

    /**
     * FORMULARIO EDITAR DIRECTOR
     * Permitido para: MANAGER, ADMIN
     */
    @GetMapping("/edit")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public String showEditForm(@RequestParam("id") Long id, Model model, RedirectAttributes redirectAttrs) {
        Director director = directorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Director no encontrado con ID: " + id));

        model.addAttribute("director", director);
        model.addAttribute("peliculas", peliculaRepository.findAll());
        return "Director/directores-form.html";
    }

    /**
     * ACTUALIZAR DIRECTOR
     * Permitido para: MANAGER, ADMIN
     */
    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public String updateDirector(@ModelAttribute("director") Director director,
                                 @RequestParam(value = "peliculas", required = false) List<Long> peliculasIds,
                                 RedirectAttributes redirectAttributes) {
        try {
            Director existingDirector = directorRepository.findById(director.getId())
                    .orElseThrow(() -> new RuntimeException("Director no encontrado"));

            existingDirector.setNombre(director.getNombre());
            existingDirector.setNacionalidad(director.getNacionalidad());

            if (peliculasIds != null) {
                List<Pelicula> peliculasSeleccionadas = peliculaRepository.findAllById(peliculasIds);
                for (Pelicula p : peliculasSeleccionadas) {
                    p.setDirector(existingDirector);
                }
                existingDirector.setPeliculas(peliculasSeleccionadas);
            }

            directorRepository.save(existingDirector);
            redirectAttributes.addFlashAttribute("successMessage", "Director actualizado correctamente.");
        } catch (Exception e) {
            logger.error("Error al actualizar director: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar el director.");
        }
        return "redirect:/directores";
    }

    /**
     * ELIMINAR DIRECTOR
     * Permitido para: MANAGER, ADMIN
     */
    @PostMapping("/delete")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public String deleteDirector(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            directorRepository.deleteById(id);
            logger.info("Director con ID {} eliminado con éxito.", id);
            redirectAttributes.addFlashAttribute("successMessage", "Director eliminado correctamente.");
        } catch (Exception e) {
            logger.error("Error al eliminar el director: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "No se puede eliminar el director (posiblemente tiene películas asociadas).");
        }
        return "redirect:/directores";
    }

    private Sort getSort(String sort) {
        if (sort == null) return Sort.by("id").ascending();
        return switch (sort) {
            case "nameAsc" -> Sort.by("nombre").ascending();
            case "nameDesc" -> Sort.by("nombre").descending();
            case "idDesc" -> Sort.by("id").descending();
            default -> Sort.by("id").ascending();
        };
    }
}
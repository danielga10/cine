package org.iesalixar.daw2.cine.controllers;

import org.iesalixar.daw2.cine.entities.Director;
import org.iesalixar.daw2.cine.entities.Funcion;
import org.iesalixar.daw2.cine.entities.Pelicula;
import org.iesalixar.daw2.cine.entities.Sala;
import org.iesalixar.daw2.cine.repositories.DirectorRepository;
import org.iesalixar.daw2.cine.repositories.FuncionRepository;
import org.iesalixar.daw2.cine.repositories.PeliculaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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


    @GetMapping()
    public String listDirectores(@RequestParam(defaultValue = "1") int page, @RequestParam(required = false) String search, @RequestParam(required = false) String sort, Model model ) {
        logger.info("Solicitando listado de todas las directores..." + search);
        Pageable pageable = PageRequest.of(page - 1, 5, getSort(sort));
        Page<Director> directores;
        int totalPages = 0;
        if (search != null && !search.isBlank()) {
            directores = directorRepository.findByNombreContainingIgnoreCase(search, pageable);
            totalPages = (int) Math.ceil((double) directorRepository.countByNombreContainingIgnoreCase(search) / 5);
        } else {
            directores = directorRepository.findAll(pageable);
            totalPages = (int) Math.ceil((double) directorRepository.count() / 5);
        }
        logger.info("Se han cargado {} directores.", directores.toList().size());
        model.addAttribute("listDirectores", directores.toList()); // Pasar la lista de directores al modelo
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        return "directores"; // Nombre de la plantilla Thymeleaf a renderizar
    }
    @GetMapping("/new")
    public String showNewForm(Model model) {
        model.addAttribute("director", new Director());
        List<Pelicula> peliculas = peliculaRepository.findAll();
        model.addAttribute("peliculas", peliculas);

        return "directores-form";
    }
    /** Formulario para editar un director existente */
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model, RedirectAttributes redirectAttrs) {
        Director director = directorRepository.findById(id).orElseThrow();
        List<Pelicula> peliculas = peliculaRepository.findAll();

        model.addAttribute("director", director);
        model.addAttribute("peliculas", peliculas);
        return "directores-form";
    }

    @PostMapping("/insert")
    public String insertDirector(@ModelAttribute("director") Director director,
                             @RequestParam("peliculas") List<Long> peliculasIds,
                             RedirectAttributes redirectAttributes) {
        try {
            List<Pelicula> peliculasSeleccionadas = peliculaRepository.findAllById(peliculasIds);

            // Asignar la sala a cada funcion antes de guardar
            for (Pelicula p : peliculasSeleccionadas) {
                p.setDirector(director);
            }

            director.setPeliculas(peliculasSeleccionadas);
            directorRepository.save(director);

            redirectAttributes.addFlashAttribute("successMessage", "Director creado correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error al crear el director.");
        }

        return "redirect:/directores";
    }

    @PostMapping("/update")
    public String updateDirector(@ModelAttribute("director") Director director,
                             @RequestParam("peliculas") List<Long> peliculasIds,
                             RedirectAttributes redirectAttributes) {
        try {
            Director existingDirector = directorRepository.findById(director.getId()).orElseThrow(() -> new RuntimeException("Pelicula no encontrada"));

            existingDirector.setNombre(director.getNombre());
            existingDirector.setNacionalidad(director.getNacionalidad());

            // Actualizar funciones seleccionadas
            List<Pelicula> peliculasSeleccionadas = peliculaRepository.findAllById(peliculasIds);

            // Asignar la sala a cada funcion
            for (Pelicula p : peliculasSeleccionadas) {
                p.setDirector(existingDirector);
            }

            existingDirector.setPeliculas(peliculasSeleccionadas);
            directorRepository.save(existingDirector);

            redirectAttributes.addFlashAttribute("successMessage", "Sala actualizada correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar la sala.");
        }

        return "redirect:/directores";
    }

    @PostMapping("/delete")
    public String deleteDirector(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            if (directorRepository == null) {
                throw new IllegalStateException("directorDAO no inyectado");
            }
            Optional<Director> director = directorRepository.findById(id);
            if (director == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Director no encontrada.");
                return "redirect:/directores";
            }
            directorRepository.deleteById(id);
            logger.info("Director con ID {} eliminada con éxito.", id);
        } catch (Exception e) {
            logger.error("Error inesperado al eliminar la director: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error interno del servidor.");
        }
        return "redirect:/directores";
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

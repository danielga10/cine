package org.iesalixar.daw2.cine.controllers;

import org.iesalixar.daw2.cine.entities.Director;
import org.iesalixar.daw2.cine.repositories.DirectorRepository;
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
@RequestMapping("/directores")
public class DirectorController {
    private static final Logger logger = LoggerFactory.getLogger(DirectorController.class);

    @Autowired
    private DirectorRepository directorRepository;

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
    public String showNewForm(org.springframework.ui.Model model, RedirectAttributes redirectAttributes) {
        model.addAttribute("director", new Director());
        try {
            List<Director> listDirectores = directorRepository.findAllWithPeliculas();
            model.addAttribute("directores", listDirectores);
        } catch (Exception e) {
            e.printStackTrace(); // imprime la causa exacta del error 500
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar directores.");
            return "redirect:/directores";
        }
        return "director-form";
    }
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, org.springframework.ui.Model model, RedirectAttributes redirectAttributes) {
        try {
            if (directorRepository == null) {
                throw new IllegalStateException("directorDAO no inyectado");
            }
            Optional<Director> director = directorRepository.findById(id);
            if (director == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Director no encontrada.");
                return "redirect:/director";
            }
            model.addAttribute("director", director);
        } catch (Exception e) {
            logger.error("Error inesperado: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error interno del servidor.");
            return "redirect:/directores";
        }
        return "director-form";
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

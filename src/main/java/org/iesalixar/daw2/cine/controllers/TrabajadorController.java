package org.iesalixar.daw2.cine.controllers;

import org.iesalixar.daw2.cine.entities.Trabajador;
import org.iesalixar.daw2.cine.repositories.TrabajadorRepository;
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
@RequestMapping("/trabajadores")
public class TrabajadorController {

    private static final Logger logger = LoggerFactory.getLogger(TrabajadorController.class);

    @Autowired
    private TrabajadorRepository trabajadorRepository;

    /** Lista todas las trabajadores */
    @GetMapping()
    public String listTrabajadores(@RequestParam(defaultValue = "1") int page, @RequestParam(required = false) String search, @RequestParam(required = false) String sort, Model model) {
        logger.info("Solicitando la lista de todas las trabajadores..." + search);
        Pageable pageable = PageRequest.of(page - 1, 5, getSort(sort));
        Page<Trabajador> trabajadores;
        int totalPages = 0;
        if (search != null && !search.isBlank()) {
            trabajadores = trabajadorRepository.findByNombreContainingIgnoreCase(search, pageable);
            totalPages = (int) Math.ceil((double) trabajadorRepository.countByNombreContainingIgnoreCase(search) / 5);
        } else {
            trabajadores = trabajadorRepository.findAll(pageable);
            totalPages = (int) Math.ceil((double) trabajadorRepository.count() / 5);
        }
        logger.info("Se han cargado {} trabajadores.", trabajadores.toList().size());
        model.addAttribute("listTrabajadores", trabajadores.toList()); // Pasar la lista de trabajadores al modelo
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        return "trabajador"; // Nombre de la plantilla Thymeleaf a renderizar
    }


    /** Formulario para crear un nuevo trabajador */
    @GetMapping("/new")
    public String showNewForm(Model model, RedirectAttributes redirectAttributes) {
        model.addAttribute("trabajador", new Trabajador());
        try {
            List<Trabajador> listTrabajadores = trabajadorRepository.findAll();
            model.addAttribute("trabajadores", listTrabajadores);
        } catch (Exception e) {
            e.printStackTrace(); // imprime la causa exacta del error 500
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar trabajadores.");
            return "redirect:/trabajadores";
        }
        return "trabajador-form";
    }

    /** Formulario para editar un trabajador existente */
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            if (trabajadorRepository == null) {
                throw new IllegalStateException("trabajadorDAO no inyectado");
            }
            Optional<Trabajador> trabajador = trabajadorRepository.findById(id);
            if (trabajador == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Trabajador no encontrada.");
                return "redirect:/trabajadores";
            }
            model.addAttribute("trabajador", trabajador);
        } catch (Exception e) {
            logger.error("Error inesperado: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error interno del servidor.");
            return "redirect:/trabajadores";
        }
        return "trabajador-form";
    }

    /** Elimina un trabajador */
    @PostMapping("/delete")
    public String deleteTrabajador(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            if (trabajadorRepository == null) {
                throw new IllegalStateException("trabajadorDAO no inyectado");
            }
            Optional<Trabajador> trabajador = trabajadorRepository.findById(id);
            if (trabajador == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Trabajador no encontrada.");
                return "redirect:/trabajadores";
            }
            trabajadorRepository.deleteById(id);
            logger.info("Trabajador con ID {} eliminada con éxito.", id);
        } catch (Exception e) {
            logger.error("Error inesperado al eliminar la trabajador: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error interno del servidor.");
        }
        return "redirect:/trabajadores";
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
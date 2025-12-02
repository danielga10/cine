package org.iesalixar.daw2.cine.controllers;

import org.iesalixar.daw2.cine.entities.Funcion;
import org.iesalixar.daw2.cine.repositories.FuncionRepository;
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

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/funciones")
public class FuncionController {

    private static final Logger logger = LoggerFactory.getLogger(FuncionController.class);

    @Autowired
    private FuncionRepository funcionRepository;

    /** Lista todas las funciones */
    @GetMapping()
    public String listFunciones(@RequestParam(defaultValue = "1") int page, @RequestParam(required = false) String search, @RequestParam(required = false) String sort, Model model) {
        logger.info("Solicitando la lista de todas las funciones..." + search);
        Pageable pageable = PageRequest.of(page - 1, 5, getSort(sort));
        Page<Funcion> funciones;
        int totalPages = 0;
        if (search != null && !search.isBlank()) {
            funciones = funcionRepository.findByCodeContainingIgnoreCase(search, pageable);
            totalPages = (int) Math.ceil((double) funcionRepository.countByCodeContainingIgnoreCase(search) / 5);
        } else {
            funciones = funcionRepository.findAll(pageable);
            totalPages = (int) Math.ceil((double) funcionRepository.count() / 5);
        }
        logger.info("Se han cargado {} funciones.", funciones.toList().size());
        model.addAttribute("listFunciones", funciones.toList()); // Pasar la lista de funciones al modelo
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        return "funciones"; // Nombre de la plantilla Thymeleaf a renderizar
    }


    /** Formulario para crear una nueva funcion */
    @GetMapping("/new")
    public String showNewForm(Model model, RedirectAttributes redirectAttributes) {
        model.addAttribute("funcion", new Funcion());
        try {
            List<Funcion> listFunciones = funcionRepository.findAll();
            model.addAttribute("funciones", listFunciones);
        } catch (Exception e) {
            e.printStackTrace(); // imprime la causa exacta del error 500
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar funciones.");
            return "redirect:/funciones";
        }
        return "funcion-form";
    }

    /** Formulario para editar un funcion existente */
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            if (funcionRepository == null) {
                throw new IllegalStateException("funcionDAO no inyectado");
            }
            Optional<Funcion> funcion = funcionRepository.findById(id);
            if (funcion == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Funcion no encontrada.");
                return "redirect:/funciones";
            }
            model.addAttribute("funcion", funcion);
        } catch (Exception e) {
            logger.error("Error inesperado: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error interno del servidor.");
            return "redirect:/funciones";
        }
        return "funcion-form";
    }

    /** Elimina un funcion */
    @PostMapping("/delete")
    public String deleteFuncion(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            if (funcionRepository == null) {
                throw new IllegalStateException("funcionDAO no inyectado");
            }
            Optional<Funcion> funcion = funcionRepository.findById(id);
            if (funcion == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Funcion no encontrada.");
                return "redirect:/funciones";
            }
            funcionRepository.deleteById(id);
            logger.info("Funcion con ID {} eliminada con éxito.", id);
        } catch (Exception e) {
            logger.error("Error inesperado al eliminar la funcion: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error interno del servidor.");
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
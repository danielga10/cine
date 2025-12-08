package org.iesalixar.daw2.cine.controllers;

import org.iesalixar.daw2.cine.entities.Sala;
import org.iesalixar.daw2.cine.entities.Trabajador;
import org.iesalixar.daw2.cine.repositories.SalaRepository;
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

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/trabajadores")
public class  TrabajadorController {

    /** Logger para registrar eventos y errores */
    private static final Logger logger = LoggerFactory.getLogger(TrabajadorController.class);

    /** Repositorio para operaciones CRUD de trabajadores */
    @Autowired
    private TrabajadorRepository trabajadorRepository;

    /** Repositorio para operaciones CRUD de salas */
    @Autowired
    private SalaRepository salaRepository;

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
        return "Trabajador/trabajadores.html"; // Nombre de la plantilla Thymeleaf a renderizar
    }


    /** Formulario para crear un nuevo trabajador */
    @GetMapping("/new")
    public String showNewForm(Model model, RedirectAttributes redirectAttributes) {
        try {
            if (!model.containsAttribute("trabajador")) {
                model.addAttribute("trabajador", new Trabajador());
            }
            List<Sala> listaSalas = salaRepository.findAll();
            model.addAttribute("listaSalas", listaSalas);
            List<Trabajador> listTrabajadores = trabajadorRepository.findAll();
            model.addAttribute("trabajadores", listTrabajadores);
        } catch (Exception e) {
            logger.error("Error al cargar salas para el formulario de trabajador: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar datos necesarios (salas).");
            return "redirect:/trabajadores";
        }
        return "Trabajador/trabajador-form.html";
    }

    /** Formulario para editar un trabajador existente */
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<Trabajador> trabajadorOpt = trabajadorRepository.findById(id);
            if (trabajadorOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Trabajador no encontrado.");
                return "redirect:/trabajadores";
            }
            // Pasa el objeto Trabajador, no el Optional
            model.addAttribute("trabajador", trabajadorOpt.get());
            // Cargar lista de salas para el select
            List<Sala> listaSalas = salaRepository.findAll();
            model.addAttribute("listaSalas", listaSalas);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error interno del servidor.");
            return "redirect:/trabajadores";
        }
        return "Trabajador/trabajador-form.html";
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

    @PostMapping("/insert")
    public String insertTrabajador(@ModelAttribute("trabajador") Trabajador trabajador,
                                   RedirectAttributes redirectAttributes) {
        if (trabajador.getSala() != null) {
            Long salaId = trabajador.getSala().getId();
            Trabajador trabajadorConMismaSala = trabajadorRepository.findBySalaId(salaId);
            if (trabajadorConMismaSala != null) {

                redirectAttributes.addFlashAttribute("errorMessage",
                        "Error: La sala " + trabajador.getSala().getNumero() +
                                " ya está asignada al trabajador: " + trabajadorConMismaSala.getNombre());
                redirectAttributes.addFlashAttribute("trabajador", trabajador);
                return "redirect:/trabajadores/new";
            }
        }
        try {
            trabajadorRepository.save(trabajador);
            redirectAttributes.addFlashAttribute("successMessage", "Trabajador creado correctamente.");
        } catch (Exception e) {
            // Capturará errores de DB como NOT NULL (si el HTML falla)
            logger.error("Error al crear el trabajador: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error al crear el trabajador: " + e.getMessage());
        }

        return "redirect:/trabajadores";
    }
    @PostMapping("/update")
    public String updateTrabajador(@ModelAttribute("trabajador") Trabajador trabajador,
                                   RedirectAttributes redirectAttributes) {
        if (trabajador.getSala() != null) {
            Trabajador trabajadorConMismaSala = trabajadorRepository.findBySalaId(trabajador.getSala().getId());

            if (trabajadorConMismaSala != null) {
                // Comprobamos si el trabajador encontrado es diferente al que estamos editando.
                if (!trabajadorConMismaSala.getId().equals(trabajador.getId())) {
                    redirectAttributes.addFlashAttribute("errorMessage",
                            "Error: La sala " + trabajador.getSala().getNumero() + " ya está asignada al trabajador: " +
                                    trabajadorConMismaSala.getNombre());

                    // Redirige al formulario con el objeto y el error de negocio
                    redirectAttributes.addFlashAttribute("trabajador", trabajador);
                    return "redirect:/trabajadores/edit?id=" + trabajador.getId();
                }
            }
        }
        try {
            Trabajador existingTrabajador = trabajadorRepository.findById(trabajador.getId())
                    .orElseThrow(() -> new RuntimeException("Trabajador no encontrado para actualizar"));
            existingTrabajador.setNombre(trabajador.getNombre());
            existingTrabajador.setTelefono(trabajador.getTelefono());
            existingTrabajador.setCorreo(trabajador.getCorreo());
            existingTrabajador.setSala(trabajador.getSala());

            trabajadorRepository.save(existingTrabajador);
            redirectAttributes.addFlashAttribute("successMessage", "Trabajador actualizado correctamente.");
        } catch (Exception e) {
            // Esta excepción ahora atrapará ERRORES DE BASE DE DATOS (como NOT NULL)
            logger.error("Error al actualizar el trabajador: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar el trabajador: " + e.getMessage());
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
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

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/trabajadores")
public class TrabajadorController {

    private static final Logger logger = LoggerFactory.getLogger(TrabajadorController.class);

    @Autowired
    private TrabajadorRepository trabajadorRepository;

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
        model.addAttribute("trabajador", new Trabajador());
        try {
            // Cargar lista de trabajadores con salas
            List<Trabajador> listTrabajadores = trabajadorRepository.findAllWithSalas();
            model.addAttribute("trabajadores", listTrabajadores);

            // Cargar lista de salas para el select
            List<Sala> listaSalas = salaRepository.findAll();
            model.addAttribute("listaSalas", listaSalas);

        } catch (Exception e) {
            e.printStackTrace(); // imprime la causa exacta del error 500
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar trabajadores o salas.");
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

    @PostMapping("/save")
    public String saveTrabajador(@ModelAttribute("trabajador") Trabajador trabajador,
                                 RedirectAttributes redirectAttributes) {
        try {
            // ----------------------------------------------------
            // 1. Lógica de Validación 1:1
            // ----------------------------------------------------
            if (trabajador.getSala() != null) {
                Long salaId = trabajador.getSala().getId();

                // Busca si existe otro trabajador con esta misma sala
                // NOTA: Debes implementar este método en tu TrabajadorRepository
                Trabajador trabajadorConMismaSala = trabajadorRepository.findBySalaId(salaId);

                // Si existe un trabajador con esa sala...
                if (trabajadorConMismaSala != null) {

                    // ... y NO es el trabajador que estamos editando actualmente:
                    boolean isDifferentTrabajador = (trabajador.getId() == null) ||
                            (!trabajadorConMismaSala.getId().equals(trabajador.getId()));

                    if (isDifferentTrabajador) {
                        redirectAttributes.addFlashAttribute("errorMessage",
                                "Error: La sala " + trabajador.getSala().getNumero() + " ya está asignada al trabajador: " +
                                        trabajadorConMismaSala.getNombre());

                        // Retorna al formulario con el objeto 'trabajador' para mantener los datos
                        redirectAttributes.addFlashAttribute("trabajador", trabajador);
                        return "redirect:/trabajadores/new"; // Redirige a /new o /edit, dependiendo del contexto
                    }
                    // Si es el mismo trabajador, la validación pasa (se le permite guardar sus datos)
                }
            }
            // ----------------------------------------------------
            // 2. Lógica de Guardado (Insert o Update)
            // ----------------------------------------------------
            if (trabajador.getId() != null) {
                // Actualizar trabajador existente (la lógica original se mantiene)
                Trabajador existingTrabajador = trabajadorRepository.findById(trabajador.getId())
                        .orElseThrow(() -> new RuntimeException("Trabajador no encontrado"));

                existingTrabajador.setNombre(trabajador.getNombre());
                existingTrabajador.setTelefono(trabajador.getTelefono());
                existingTrabajador.setCorreo(trabajador.getCorreo());

                // Si la validación pasó, actualizamos la sala
                existingTrabajador.setSala(trabajador.getSala());

                trabajadorRepository.save(existingTrabajador);
                redirectAttributes.addFlashAttribute("successMessage", "Trabajador actualizado correctamente.");
            } else {
                // Crear nuevo trabajador
                trabajadorRepository.save(trabajador);
                redirectAttributes.addFlashAttribute("successMessage", "Trabajador creado correctamente.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error al guardar el trabajador: " + e.getMessage());
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
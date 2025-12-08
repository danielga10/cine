package org.iesalixar.daw2.cine.controllers;

import jakarta.validation.Valid; // Necesario para validar los @NotNull de la entidad
import org.iesalixar.daw2.cine.entities.Boleto;
import org.iesalixar.daw2.cine.repositories.BoletoRepository;
import org.iesalixar.daw2.cine.repositories.ClienteRepository;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/boletos")
public class BoletoController {
    private static final Logger logger = LoggerFactory.getLogger(BoletoController.class);

    @Autowired
    private BoletoRepository boletoRepository;

    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private FuncionRepository funcionRepository;

    @GetMapping()
    public String listBoletos(@RequestParam(defaultValue = "1") int page, @RequestParam(required = false) String search, @RequestParam(required = false) String sort, Model model ) {
        logger.info("Solicitando listado de todas las boletos..." + search);
        Pageable pageable = PageRequest.of(page - 1, 5, getSort(sort));
        Page<Boleto> boletos;
        int totalPages = 0;
        if (search != null && !search.isBlank()) {
            boletos = boletoRepository.findByAsientoContainingIgnoreCase(search, pageable);
            totalPages = (int) Math.ceil((double) boletoRepository.countByAsientoContainingIgnoreCase(search) / 5);
        } else {
            boletos = boletoRepository.findAll(pageable);
            totalPages = (int) Math.ceil((double) boletoRepository.count() / 5);
        }
        logger.info("Se han cargado {} boletos.", boletos.toList().size());
        model.addAttribute("listBoletos", boletos.toList());
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        return "Boleto/boletos.html";
    }

    @GetMapping("/new")
    public String showNewForm(@RequestParam(required = false) Long funcionId, Model model, RedirectAttributes redirectAttributes) {
        Boleto boleto = new Boleto();
        
        // Si se proporciona un funcionId, pre-seleccionar la función
        if (funcionId != null) {
            Optional<org.iesalixar.daw2.cine.entities.Funcion> funcion = funcionRepository.findById(funcionId);
            if (funcion.isPresent()) {
                boleto.setFuncion(funcion.get());
                model.addAttribute("selectedFuncionId", funcionId);
            }
        }
        
        model.addAttribute("boleto", boleto);
        try {
            List<Boleto> listBoletos = boletoRepository.findAll();
            model.addAttribute("boletos", listBoletos);

            // Cargar listas para los desplegables
            model.addAttribute("listaClientes", clienteRepository.findAll());
            model.addAttribute("listaFunciones", funcionRepository.findAll());

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar datos.");
            return "redirect:/boletos";
        }
        return "Boleto/boleto-form.html";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            if (boletoRepository == null) {
                throw new IllegalStateException("boletoDAO no inyectado");
            }
            Optional<Boleto> boleto = boletoRepository.findById(id);

            // Optional nunca es null, hay que usar isEmpty() o isPresent()
            if (boleto.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Boleto no encontrada.");
                return "redirect:/boletos";
            }

            // Hay que pasar boleto.get(), no el Optional entero
            model.addAttribute("boleto", boleto.get());

            model.addAttribute("listaClientes", clienteRepository.findAll());
            model.addAttribute("listaFunciones", funcionRepository.findAll());

        } catch (Exception e) {
            logger.error("Error inesperado: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error interno del servidor.");
            return "redirect:/boletos";
        }
        return "Boleto/boleto-form.html";
    }

    @PostMapping("/insert")
    public String insertBoleto(@Valid @ModelAttribute("boleto") Boleto boleto,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        logger.info("Intentando insertar nuevo boleto.");

        if (bindingResult.hasErrors()) {
            // Si hay errores de validación, recargar datos para la vista
            model.addAttribute("listaClientes", clienteRepository.findAll());
            model.addAttribute("listaFunciones", funcionRepository.findAll());
            return "Boleto/boleto-form.html";
        }

        try {
            boletoRepository.save(boleto);
            // MENSAJE DE ÉXITO ELIMINADO

        } catch (Exception e) {
            logger.error("Error al crear el boleto: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error al crear el boleto. Verifique que no haya duplicados (código, etc).");
        }
        return "redirect:/boletos";
    }

    // Método para Actualizar un Boleto existente
    @PostMapping("/update")
    public String updateBoleto(@Valid @ModelAttribute("boleto") Boleto boleto,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        logger.info("Intentando actualizar boleto con ID: {}", boleto.getId());

        if (bindingResult.hasErrors()) {
            // Si hay errores de validación, recargar datos para la vista
            model.addAttribute("listaClientes", clienteRepository.findAll());
            model.addAttribute("listaFunciones", funcionRepository.findAll());
            return "Boleto/boleto-form.html";
        }

        try {
            // 1. Recuperar el boleto existente
            Boleto existingBoleto = boletoRepository.findById(boleto.getId())
                    .orElseThrow(() -> new RuntimeException("Boleto no encontrado para actualizar."));

            // 2. Actualizar campos
            existingBoleto.setCode(boleto.getCode());
            existingBoleto.setAsiento(boleto.getAsiento());
            existingBoleto.setPrecio(boleto.getPrecio());
            existingBoleto.setCliente(boleto.getCliente());
            existingBoleto.setFuncion(boleto.getFuncion());

            boletoRepository.save(existingBoleto);
            // MENSAJE DE ÉXITO ELIMINADO

        } catch (Exception e) {
            logger.error("Error al actualizar el boleto con ID {}: {}", boleto.getId(), e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar el boleto. El ID podría ser inválido.");
        }
        return "redirect:/boletos";
    }

    @PostMapping("/delete")
    public String deleteBoleto(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            if (boletoRepository == null) {
                throw new IllegalStateException("boletoDAO no inyectado");
            }
            Optional<Boleto> boleto = boletoRepository.findById(id);

            if (boleto.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Boleto no encontrada.");
                return "redirect:/boletos";
            }
            boletoRepository.deleteById(id);
            logger.info("Boleto con ID {} eliminada con éxito.", id);
        } catch (Exception e) {
            logger.error("Error inesperado al eliminar la boleto: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error interno del servidor.");
        }
        return "redirect:/boletos";
    }

    private Sort getSort(String sort) {
        if (sort == null) {
            return Sort.by("id").ascending();
        }
        return switch (sort) {
            case "nameAsc" -> Sort.by("asiento").ascending();
            case "nameDesc" -> Sort.by("asiento").descending();
            case "idDesc" -> Sort.by("id").descending();
            default -> Sort.by("id").ascending();
        };
    }
}
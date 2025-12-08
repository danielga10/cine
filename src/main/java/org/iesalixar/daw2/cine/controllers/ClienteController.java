package org.iesalixar.daw2.cine.controllers;

import org.iesalixar.daw2.cine.entities.Boleto;
import org.iesalixar.daw2.cine.entities.Cliente;
import org.iesalixar.daw2.cine.repositories.BoletoRepository;
import org.iesalixar.daw2.cine.repositories.ClienteRepository;
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

/**
 * Controlador REST para gestionar las operaciones CRUD de clientes.
 * Maneja las peticiones HTTP relacionadas con la creación, lectura, actualización y eliminación de clientes.
 * 
 * @author IES Alixar DAW2
 * @version 1.0
 */
@Controller
@RequestMapping("/clientes")
public class ClienteController {
    /** Logger para registrar eventos y errores */
    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);

    /** Repositorio para operaciones CRUD de clientes */
    @Autowired
    private ClienteRepository clienteRepository;
    
    /** Repositorio para operaciones CRUD de boletos */
    @Autowired
    private BoletoRepository boletoRepository;

    @GetMapping()
    public String listClientes(@RequestParam(defaultValue = "1") int page, @RequestParam(required = false) String search, @RequestParam(required = false) String sort, Model model ) {
        logger.info("Solicitando listado de todas las clientes..." + search);
        Pageable pageable = PageRequest.of(page - 1, 5, getSort(sort));
        Page<Cliente> clientes;
        int totalPages = 0;
        if (search != null && !search.isBlank()) {
            clientes = clienteRepository.findByEmailContainingIgnoreCase(search, pageable);
            totalPages = (int) Math.ceil((double) clienteRepository.countByEmailContainingIgnoreCase(search) / 5);
        } else {
            clientes = clienteRepository.findAll(pageable);
            totalPages = (int) Math.ceil((double) clienteRepository.count() / 5);
        }
        logger.info("Se han cargado {} peliculas.", clientes.toList().size());
        model.addAttribute("listClientes", clientes.toList()); // Pasar la lista de clientes al modelo
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        return "Cliente/clientes.html"; // Nombre de la plantilla Thymeleaf a renderizar
    }
    @GetMapping("/new")
    public String showNewForm(org.springframework.ui.Model model, RedirectAttributes redirectAttributes) {
        model.addAttribute("cliente", new Cliente());
        try {
            // Cargar la lista de boletos para el select
            List<Boleto> listaBoletos = boletoRepository.findAll();
            model.addAttribute("listaBoletos", listaBoletos); // <-- Añadido
        } catch (Exception e) {
            logger.error("Error al cargar la lista de boletos:", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar datos necesarios.");
            return "redirect:/clientes";
        }
        return "Cliente/cliente-form.html";
    }
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, org.springframework.ui.Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<Cliente> clienteOpt = clienteRepository.findById(id);

            if (clienteOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Cliente no encontrado.");
                return "redirect:/clientes";
            }

            model.addAttribute("cliente", clienteOpt.get());

            // Cargar la lista de boletos para el select
            List<Boleto> listaBoletos = boletoRepository.findAll();
            model.addAttribute("listaBoletos", listaBoletos); // <-- Añadido

        } catch (Exception e) {
            logger.error("Error inesperado al cargar cliente para edición: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error interno del servidor.");
            return "redirect:/clientes";
        }
        return "Cliente/cliente-form.html";
    }
    @PostMapping("/insert")
    public String insertCliente(@ModelAttribute("cliente") Cliente cliente,
                                RedirectAttributes redirectAttributes) {
        try {
            // NOTA: Se asume que la entidad Cliente tiene validación de unicidad de email/nombre
            clienteRepository.save(cliente);
            logger.info("Cliente con ID {} creado con éxito.", cliente.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Cliente creado correctamente.");
        } catch (Exception e) {
            logger.error("Error al crear el cliente: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error al crear el cliente. Verifique los datos.");
        }
        return "redirect:/clientes";
    }

    // ----------------------------------------------------
    // NUEVO: Método para Actualizar Cliente
    // ----------------------------------------------------
    @PostMapping("/update")
    public String updateCliente(@ModelAttribute("cliente") Cliente cliente,
                                RedirectAttributes redirectAttributes) {
        try {
            // 1. Obtener el cliente existente para asegurar que no se sobrescriban datos sensibles (si los hubiera)
            Cliente existingCliente = clienteRepository.findById(cliente.getId())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado para actualizar."));

            // 2. Actualizar solo los campos modificables (nombre y email, por ejemplo)
            existingCliente.setNombre(cliente.getNombre());
            existingCliente.setEmail(cliente.getEmail());

            // 3. Guardar la entidad actualizada
            clienteRepository.save(existingCliente);
            logger.info("Cliente con ID {} actualizado con éxito.", cliente.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Cliente actualizado correctamente.");
        } catch (Exception e) {
            logger.error("Error al actualizar el cliente con ID {}: {}", cliente.getId(), e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar el cliente. Verifique los datos.");
        }
        return "redirect:/clientes";
    }
    @PostMapping("/delete")
    public String deleteCliente(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            if (clienteRepository == null) {
                throw new IllegalStateException("clienteDAO no inyectado");
            }
            Optional<Cliente> cliente = clienteRepository.findById(id);
            if (cliente == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Cliente no encontrada.");
                return "redirect:/clientes";
            }
            clienteRepository.deleteById(id);
            logger.info("Cliente con ID {} eliminada con éxito.", id);
        } catch (Exception e) {
            logger.error("Error inesperado al eliminar la cliente: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error interno del servidor.");
        }
        return "redirect:/clientes";
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

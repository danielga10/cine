package org.iesalixar.daw2.cine.controllers;


import org.iesalixar.daw2.cine.entities.Pelicula;
import org.iesalixar.daw2.cine.entities.Cliente;
import org.iesalixar.daw2.cine.entities.Funcion;
import org.iesalixar.daw2.cine.repositories.ClienteRepository;
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
@RequestMapping("/clientes")
public class ClienteController {
    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);

    @Autowired
    private ClienteRepository clienteRepository;

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
        return "clientes"; // Nombre de la plantilla Thymeleaf a renderizar
    }
    @GetMapping("/new")
    public String showNewForm(org.springframework.ui.Model model, RedirectAttributes redirectAttributes) {
        model.addAttribute("cliente", new Cliente());
        try {
            List<Cliente> listClientes = clienteRepository.findAll();
            model.addAttribute("clientes", listClientes);
        } catch (Exception e) {
            e.printStackTrace(); // imprime la causa exacta del error 500
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar clientes.");
            return "redirect:/clientes";
        }
        return "cliente-form";
    }
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, org.springframework.ui.Model model, RedirectAttributes redirectAttributes) {
        try {
            if (clienteRepository == null) {
                throw new IllegalStateException("clienteDAO no inyectado");
            }
            Optional<Cliente> cliente = clienteRepository.findById(id);
            if (cliente == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Cliente no encontrada.");
                return "redirect:/clientes";
            }
            model.addAttribute("cliente", cliente);
        } catch (Exception e) {
            logger.error("Error inesperado: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error interno del servidor.");
            return "redirect:/clientes";
        }
        return "cliente-form";
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

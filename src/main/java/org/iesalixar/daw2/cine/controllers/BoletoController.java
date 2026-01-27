package org.iesalixar.daw2.cine.controllers;

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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/boletos")
public class BoletoController {
    /** Logger para registrar eventos y errores */
    private static final Logger logger = LoggerFactory.getLogger(BoletoController.class);

    /** Repositorio para operaciones CRUD de boletos */
    @Autowired
    private BoletoRepository boletoRepository;

    /** Repositorio para operaciones CRUD de clientes */
    @Autowired
    private ClienteRepository clienteRepository;
    
    /** Repositorio para operaciones CRUD de funciones */
    @Autowired
    private FuncionRepository funcionRepository;

    /**
     * Muestra la lista paginada de boletos con opciones de búsqueda y ordenamiento.
     * 
     * @param page Número de página (por defecto 1)
     * @param search Término de búsqueda para filtrar por asiento
     * @param sort Tipo de ordenamiento (nameAsc, nameDesc, idDesc)
     * @param model Modelo para pasar datos a la vista
     * @return Nombre de la plantilla Thymeleaf a renderizar
     */
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

    /**
     * Muestra el formulario para crear un nuevo boleto.
     * Si se proporciona un funcionId, pre-selecciona la función en el formulario.
     * 
     * @param funcionId ID opcional de la función a pre-seleccionar
     * @param model Modelo para pasar datos a la vista
     * @param redirectAttributes Atributos para mensajes flash en caso de error
     * @return Nombre de la plantilla del formulario
     */
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

    /**
     * Muestra el formulario para editar un boleto existente.
     * 
     * @param id ID del boleto a editar
     * @param model Modelo para pasar datos a la vista
     * @param redirectAttributes Atributos para mensajes flash en caso de error
     * @return Nombre de la plantilla del formulario o redirección si no se encuentra el boleto
     */
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

    /**
     * Inserta un nuevo boleto en la base de datos.
     * 
     * @param boleto Objeto boleto con los datos a insertar
     * @param model Modelo para pasar datos a la vista en caso de error
     * @param redirectAttributes Atributos para mensajes flash
     * @return Redirección a la lista de boletos
     */
    @PostMapping("/insert")
    public String insertBoleto(@ModelAttribute("boleto") Boleto boleto,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        logger.info("Intentando insertar nuevo boleto.");
        try {
            boletoRepository.save(boleto);

        } catch (Exception e) {
            // Este catch capturará fallos de la base de datos (ej. NOT NULL) si la validación HTML falla.
            logger.error("Error al crear el boleto: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error al crear el boleto. Verifique los datos (campos vacíos, código duplicado, etc).");
        }
        return "redirect:/boletos";
    }
    /**
     * Actualiza un boleto existente en la base de datos.
     * 
     * @param boleto Objeto boleto con los datos actualizados
     * @param model Modelo para pasar datos a la vista en caso de error
     * @param redirectAttributes Atributos para mensajes flash
     * @return Redirección a la lista de boletos
     */
    @PostMapping("/update")
    public String updateBoleto(@ModelAttribute("boleto") Boleto boleto,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        logger.info("Intentando actualizar boleto con ID: {}", boleto.getId());
        try {
            Boleto existingBoleto = boletoRepository.findById(boleto.getId())
                    .orElseThrow(() -> new RuntimeException("Boleto no encontrado para actualizar."));

            // 2. Actualizar campos
            existingBoleto.setCode(boleto.getCode());
            existingBoleto.setAsiento(boleto.getAsiento());
            existingBoleto.setPrecio(boleto.getPrecio());
            existingBoleto.setCliente(boleto.getCliente());
            existingBoleto.setFuncion(boleto.getFuncion());

            boletoRepository.save(existingBoleto);

        } catch (Exception e) {
            // Este catch capturará fallos de la base de datos (ej. NOT NULL o ID inválido).
            logger.error("Error al actualizar el boleto con ID {}: {}", boleto.getId(), e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar el boleto. El ID podría ser inválido o faltan datos.");
        }
        return "redirect:/boletos";
    }

    /**
     * Elimina un boleto de la base de datos.
     * 
     * @param id ID del boleto a eliminar
     * @param redirectAttributes Atributos para mensajes flash
     * @return Redirección a la lista de boletos
     */
    @PreAuthorize("hasRole('ADMIN')")
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

    /**
     * Método auxiliar para obtener el objeto Sort según el parámetro de ordenamiento.
     * 
     * @param sort Tipo de ordenamiento (nameAsc, nameDesc, idDesc)
     * @return Objeto Sort configurado
     */
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
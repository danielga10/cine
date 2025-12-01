package org.iesalixar.daw2.cine.controllers;



import org.iesalixar.daw2.cine.entities.Boleto;
import org.iesalixar.daw2.cine.repositories.BoletoRepository;
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
@RequestMapping("/boletos")
public class BoletoController {
    private static final Logger logger = LoggerFactory.getLogger(BoletoController.class);

    @Autowired
    private BoletoRepository boletoRepository;

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
        model.addAttribute("listBoletos", boletos.toList()); // Pasar la lista de boletos al modelo
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        return "boletos"; // Nombre de la plantilla Thymeleaf a renderizar
    }
    @GetMapping("/new")
    public String showNewForm(org.springframework.ui.Model model, RedirectAttributes redirectAttributes) {
        model.addAttribute("boleto", new Boleto());
        try {
            List<Boleto> listBoletos = boletoRepository.findAll();
            model.addAttribute("boletos", listBoletos);
        } catch (Exception e) {
            e.printStackTrace(); // imprime la causa exacta del error 500
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar boletos.");
            return "redirect:/boletos";
        }
        return "boleto-form";
    }
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, org.springframework.ui.Model model, RedirectAttributes redirectAttributes) {
        try {
            if (boletoRepository == null) {
                throw new IllegalStateException("boletoDAO no inyectado");
            }
            Optional<Boleto> boleto = boletoRepository.findById(id);
            if (boleto == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Boleto no encontrada.");
                return "redirect:/boletos";
            }
            model.addAttribute("boleto", boleto);
        } catch (Exception e) {
            logger.error("Error inesperado: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error interno del servidor.");
            return "redirect:/boletos";
        }
        return "boleto-form";
    }
    @PostMapping("/delete")
    public String deleteBoleto(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            if (boletoRepository == null) {
                throw new IllegalStateException("boletoDAO no inyectado");
            }
            Optional<Boleto> boleto = boletoRepository.findById(id);
            if (boleto == null) {
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
            case "nameAsc" -> Sort.by("name").ascending();
            case "nameDesc" -> Sort.by("name").descending();
            case "idDesc" -> Sort.by("id").descending();
            default -> Sort.by("id").ascending();
        };
    }
}

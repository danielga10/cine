package org.iesalixar.daw2.cine.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class LoginController {
    /**
     * Maneja las solicitudes GET a la página de inicio de sesión.
     * Recupera mensajes de error de la sesión, si existen, y los pasa al modelo
     * para ser mostrados en la vista de login.
     *
     * @param request El objeto {@link HttpServletRequest} que contiene la solicitud
     *                HTTP.
     * @param model   El objeto {@link Model} que se utiliza para pasar atributos a
     *                la vista.
     * @return El nombre de la plantilla de Thymeleaf que renderiza la página de
     * login.
     */
    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {
        // Recuperar el mensaje de error desde la sesión
        String errorMessage = (String)
                request.getSession().getAttribute("errorMessage");
        // Si existe un mensaje de error, añadirlo al modelo y limpiar la sesión
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            request.getSession().removeAttribute("errorMessage"); // Limpiar elmensaje después de mostrarlo
        }
        return "login"; // Redirige a una plantilla personalizada de login
    }
}
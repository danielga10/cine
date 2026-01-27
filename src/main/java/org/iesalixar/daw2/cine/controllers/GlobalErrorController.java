package org.iesalixar.daw2.cine.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.http.HttpStatus;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
/**
 * Controlador global para manejar errores HTTP y redirigir a páginas de error
 personalizadas.
 * Este controlador detecta los códigos de estado de los errores comunes (403,
 404, 500)
 * y redirige a plantillas específicas, proporcionando una experiencia de usuario
 más amigable.
 * También maneja otros errores no especificados, redirigiendo a una página de
 error genérica.
 */
@Controller
public class GlobalErrorController implements ErrorController {
    private static final Logger logger =
            LoggerFactory.getLogger(GlobalErrorController.class);
    /**
     * Maneja las solicitudes a la ruta "/error" y redirige a la página de error
     correspondiente
     * según el código de estado HTTP del error.
     *
     * @param request La solicitud HTTP que contiene información sobre el error.
     * @param model El modelo para pasar datos a la vista de error.
     * @return La vista de error correspondiente según el código de estado, o
    una vista de error genérica
     * si no se reconoce el código.
     */
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status =
                request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        logger.info("Accediendo al método handleError");
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            // Manejar error 403 - Acceso denegado
            if (statusCode == HttpStatus.FORBIDDEN.value()) {
                logger.warn("Error 403: Acceso denegado.");
                model.addAttribute("errorCode", "403");
                model.addAttribute("errorMessage", "No tienes permisos para acceder a esta página.");
                return "error/403"; //Redirige al template de página prohibida
            }
            // Manejar error 404 - No encontrado
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                logger.warn("Error 404: Página no encontrada.");
                model.addAttribute("errorCode", "404");
                model.addAttribute("errorMessage", "La página que buscas no se ha encontrado.");
                return "error/404"; //Redirige al template de página no encontrada
            }
            // Manejar error 500 - Error interno del servidor
            if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                logger.error("Error 500: Error interno del servidor.");
                model.addAttribute("errorCode", "500");
                model.addAttribute("errorMessage", "Ha ocurrido un error interno en el servidor.");
                return "error/500"; //Redirige al template de error interno del servidor
            }
        }
        // Redirigir a una página de error genérica si no se reconoce el código de estado
        logger.error("Error desconocido: Redirigiendo a la página de error genérica.");
                model.addAttribute("errorCode", "Error");
        model.addAttribute("errorMessage", "Ha ocurrido un error inesperado.");
        return "error/generic";
    }
}
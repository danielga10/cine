package org.iesalixar.daw2.cine.controllers;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.model.OAuth2AccessToken;
import jakarta.servlet.http.HttpSession;
import org.iesalixar.daw2.cine.dtos.UserDTO;
import org.iesalixar.daw2.cine.services.DiscordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private DiscordService discordService;

    // Inicia el proceso de login
    @GetMapping("/login/discord")
    public String redirectToDiscord() {
        return "redirect:" + discordService.getAuthorizationUrl();
    }

    // Callback donde Discord nos devuelve el código
    @GetMapping("/login/discord/callback")
    public String callback(@RequestParam("code") String code, HttpSession session) {
        try {
            // 1. Obtener Token
            OAuth2AccessToken token = discordService.getAccessToken(code);

            // 2. Obtener JSON del perfil
            String jsonPerfil = discordService.getUserData(token);

            // 3. Mapear JSON a Objeto Java
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            UserDTO user = mapper.readValue(jsonPerfil, UserDTO.class);

            // 4. Guardar en la sesión para usarlo en el Header
            session.setAttribute("usuarioLogueado", user);

            return "redirect:/"; // Volver a la página principal
        } catch (Exception e) {
            return "redirect:/error?msg=auth_failed";
        }
    }

    // Ruta para cerrar sesión
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Borra todos los datos de la sesión
        return "redirect:/";
    }
}

package org.iesalixar.daw2.cine.controllers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.model.OAuth2AccessToken;
import jakarta.servlet.http.HttpSession;
import org.iesalixar.daw2.cine.dtos.DiscordUserDTO;
import org.iesalixar.daw2.cine.entities.User;
import org.iesalixar.daw2.cine.repositories.RoleRepository;
import org.iesalixar.daw2.cine.repositories.UserRepository;
import org.iesalixar.daw2.cine.services.DiscordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller; // Importante añadir esto
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;

@Controller // Necesario para que Spring detecte los @GetMapping
public class AuthDiscordController {

    @Autowired
    private DiscordService discordService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Redirige al usuario a la página de autorización de Discord.
     */
    @GetMapping("/login/discord")
    public String redirectToDiscord() {
        return "redirect:" + discordService.getAuthorizationUrl();
    }

    /**
     * Callback que recibe el código de Discord y gestiona la autenticación.
     */
    @GetMapping("/login/discord/callback")
    public String callback(@RequestParam("code") String code, HttpSession session) {
        try {
            // 1. Obtener Token
            OAuth2AccessToken token = discordService.getAccessToken(code);

            // 2. Obtener Perfil
            String jsonPerfil = discordService.getUserData(token);

            // 3. Mapear a DTO
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            DiscordUserDTO discordDTO = mapper.readValue(jsonPerfil, DiscordUserDTO.class);

            // 4. Lógica de Base de Datos (Sin Email)
            User user = userRepository.findByDiscordId(discordDTO.getId())
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setDiscordId(discordDTO.getId());
                        newUser.setUsername(discordDTO.getUsername());
                        newUser.setFirstName(discordDTO.getUsername());
                        newUser.setLastName("DiscordUser");
                        newUser.setEnabled(true);

                        // Asignar rol por defecto
                        roleRepository.findByName("ROLE_USER").ifPresent(role -> {
                            newUser.setRoles(Collections.singleton(role));
                        });

                        return newUser;
                    });

            // Actualizamos solo el username (ya que no hay email en la entidad)
            user.setUsername(discordDTO.getUsername());
            userRepository.save(user);

            // 5. Autenticación en Spring Security
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 6. Sesión para Thymeleaf
            session.setAttribute("usuarioLogueado", user);

            return "redirect:/";

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/login?error=auth_failed";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        SecurityContextHolder.clearContext();
        session.invalidate();
        return "redirect:/login?logout=true";
    }
}
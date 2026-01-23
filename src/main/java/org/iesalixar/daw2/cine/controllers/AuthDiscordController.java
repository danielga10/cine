package org.iesalixar.daw2.cine.controllers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.model.OAuth2AccessToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.iesalixar.daw2.cine.dtos.DiscordUserDTO;
import org.iesalixar.daw2.cine.entities.User;
import org.iesalixar.daw2.cine.repositories.RoleRepository;
import org.iesalixar.daw2.cine.repositories.UserRepository;
import org.iesalixar.daw2.cine.services.DiscordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;

@Controller
public class AuthDiscordController {

    private static final Logger logger = LoggerFactory.getLogger(AuthDiscordController.class);

    @Autowired
    private DiscordService discordService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    @GetMapping("/login/discord")
    public String redirectToDiscord() {
        logger.info("Redirigiendo a Discord OAuth");
        return "redirect:" + discordService.getAuthorizationUrl();
    }

    @GetMapping("/login/discord/callback")
    public String callback(@RequestParam("code") String code,
                          HttpServletRequest request,
                          HttpServletResponse response,
                          HttpSession session) {
        try {
            logger.info("=== INICIO Discord Callback ===");
            logger.info("Code recibido: {}", code);

            OAuth2AccessToken token = discordService.getAccessToken(code);
            logger.info("Token obtenido");

            String jsonPerfil = discordService.getUserData(token);
            logger.info("Perfil obtenido: {}", jsonPerfil);

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            DiscordUserDTO discordDTO = mapper.readValue(jsonPerfil, DiscordUserDTO.class);
            logger.info("Discord User: id={}, username={}", discordDTO.getId(), discordDTO.getUsername());

            User user = userRepository.findByDiscordId(discordDTO.getId())
                    .orElseGet(() -> {
                        logger.info("Creando nuevo usuario Discord");
                        User newUser = new User();
                        newUser.setDiscordId(discordDTO.getId());
                        newUser.setUsername(discordDTO.getUsername());
                        newUser.setPassword("");
                        newUser.setProvider("discord");
                        newUser.setFirstName(discordDTO.getUsername());
                        newUser.setLastName("Discord");
                        newUser.setEnabled(true);

                        roleRepository.findByName("ROLE_USER").ifPresent(role -> {
                            newUser.setRoles(Collections.singleton(role));
                        });

                        return newUser;
                    });

            user.setUsername(discordDTO.getUsername());
            if (user.getProvider() == null || user.getProvider().isEmpty()) {
                user.setProvider("discord");
            }
            userRepository.save(user);
            logger.info("Usuario guardado: {}", user.getUsername());

            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            logger.info("UserDetails cargado con authorities: {}", userDetails.getAuthorities());

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);

            securityContextRepository.saveContext(securityContext, request, response);

            session.setAttribute("usuarioLogueado", user);

            logger.info("Autenticación Discord completada, redirigiendo a /");
            logger.info("=== FIN Discord Callback ===");

            return "redirect:/";

        } catch (Exception e) {
            logger.error("Error en Discord callback", e);
            e.printStackTrace();
            return "redirect:/login?error=discord_auth_failed";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        SecurityContextHolder.clearContext();
        session.invalidate();
        return "redirect:/login?logout=true";
    }
}

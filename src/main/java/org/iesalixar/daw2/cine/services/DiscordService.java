package org.iesalixar.daw2.cine.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.apis.DiscordApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Service // Indica a Spring que esta clase es un componente de servicio
public class DiscordService {

    // Cargamos los valores desde application.properties o variables de entorno
    @Value("${discord.client.id}")
    private String clientId;

    @Value("${discord.client.secret}")
    private String clientSecret;

    @Value("${discord.redirect.uri}")
    private String redirectUri;

    private OAuth20Service service;

    /**
     * Inicializa el servicio OAuth2 una vez que Spring ha inyectado todas las dependencias.
     */
    @PostConstruct
    public void init() {
        // Imprime en consola para verificar que el ID se carga como Snowflake (número) y no como texto
        System.out.println("DiscordService - Client ID cargado: " + clientId);

        this.service = new ServiceBuilder(clientId)
                .apiSecret(clientSecret)
                .defaultScope("identify email") // Permisos necesarios para obtener el perfil y correo
                .callback(redirectUri)
                .build(DiscordApi.instance());
    }

    /**
     * Genera la URL de autorización de Discord a la que debemos redirigir al usuario.
     */
    public String getAuthorizationUrl() {
        return service.getAuthorizationUrl();
    }

    /**
     * Intercambia el código temporal recibido en el callback por un token de acceso.
     */
    public OAuth2AccessToken getAccessToken(String code) throws IOException, ExecutionException, InterruptedException {
        return service.getAccessToken(code);
    }

    /**
     * Realiza una petición autenticada a la API de Discord para obtener los datos del usuario logueado.
     * @return String JSON con los datos del perfil (id, username, email, etc.)
     */
    public String getUserData(OAuth2AccessToken token) throws IOException, ExecutionException, InterruptedException {
        // Endpoint estándar de Discord para obtener el perfil del usuario actual
        OAuthRequest request = new OAuthRequest(Verb.GET, "https://discord.com/api/users/@me");

        // Firma la petición con el token de acceso obtenido anteriormente
        service.signRequest(token, request);

        try (Response response = service.execute(request)) {
            if (response.getCode() != 200) {
                throw new IOException("Error al obtener datos de Discord. Código: " + response.getCode());
            }
            return response.getBody();
        }
    }
}
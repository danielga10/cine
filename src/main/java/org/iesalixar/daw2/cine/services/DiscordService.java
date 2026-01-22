package org.iesalixar.daw2.cine.services;

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

@Service
public class DiscordService {
    @Value("${discord.client.id}") private String clientId;
    @Value("${discord.client.secret}") private String clientSecret;
    @Value("${discord.redirect.uri}") private String redirectUri;

    private OAuth20Service service;

    @PostConstruct
    public void init() {
        this.service = new ServiceBuilder(clientId)
                .apiSecret(clientSecret)
                .defaultScope("identify email")
                .callback(redirectUri)
                .build(DiscordApi.instance());
    }

    public String getAuthorizationUrl() {
        return service.getAuthorizationUrl();
    }

    public OAuth2AccessToken getAccessToken(String code) throws IOException, ExecutionException, InterruptedException {
        return service.getAccessToken(code);
    }

    public String getUserData(OAuth2AccessToken token) throws IOException, ExecutionException, InterruptedException {
        OAuthRequest request = new OAuthRequest(Verb.GET, "https://discord.com/api/users/@me");
        service.signRequest(token, request);
        try (Response response = service.execute(request)) {
            return response.getBody();
        }
    }
}

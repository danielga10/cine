package org.iesalixar.daw2.cine.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DebugController {
    
    private static final Logger logger = LoggerFactory.getLogger(DebugController.class);
    
    @Value("${spring.security.oauth2.client.registration.gitlab.client-id:NOT_SET}")
    private String gitlabClientId;
    
    @Value("${spring.security.oauth2.client.registration.gitlab.client-secret:NOT_SET}")
    private String gitlabClientSecret;
    
    @GetMapping("/debug/oauth2")
    @ResponseBody
    public String debugOAuth2() {
        StringBuilder sb = new StringBuilder();
        sb.append("GitLab Client ID: ").append(gitlabClientId).append("\n");
        sb.append("GitLab Client Secret: ").append(gitlabClientSecret).append("\n");
        
        logger.info("GitLab Client ID: {}", gitlabClientId);
        logger.info("GitLab Client Secret: {}", gitlabClientSecret);
        
        return sb.toString();
    }
}

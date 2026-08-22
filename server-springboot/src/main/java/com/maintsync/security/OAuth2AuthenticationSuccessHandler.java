package com.maintsync.security;

import com.maintsync.model.User;
import com.maintsync.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Value("${app.oauth2.authorized-redirect-uri:http://localhost:5173/login}")
    private String redirectUri;

    public OAuth2AuthenticationSuccessHandler(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        if (name == null || name.isBlank()) {
            name = (String) attributes.get("login");
        }
        if (name == null || name.isBlank()) {
            name = "OAuth User";
        }
        if (email == null || email.isBlank()) {
            email = attributes.get("login") + "@github.oauth.user";
        }

        Optional<User> userOpt = userRepository.findByEmail(email.toLowerCase().trim());
        User user;
        if (userOpt.isPresent()) {
            user = userOpt.get();
        } else {
            user = User.builder()
                    .fullName(name)
                    .email(email.toLowerCase().trim())
                    .role("employee")
                    .authProvider("OAUTH2")
                    .providerId(String.valueOf(attributes.get("id") != null ? attributes.get("id") : attributes.get("sub")))
                    .build();
            user = userRepository.save(user);
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole(), user.getFullName());

        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("token", token)
                .queryParam("email", user.getEmail())
                .queryParam("role", user.getRole())
                .queryParam("name", user.getFullName())
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}

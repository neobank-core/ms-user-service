package org.neobank.userservice.service;

import lombok.RequiredArgsConstructor;
import org.neobank.userservice.dto.LoginRequest;
import org.neobank.userservice.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    private final RestClient restClient = RestClient.create();

    public LoginResponse login(LoginRequest request) {
        String url =
                serverUrl +
                        "/realms/" +
                        realm +
                        "/protocol/openid-connect/token";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("username", request.username());
        body.add("password", request.password());

        Map<String, Object> response =
                restClient.post()
                        .uri(url)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .body(body)
                        .retrieve()
                        .body(Map.class);
        return new LoginResponse(
                (String) response.get("access_token"),
                (String) response.get("refresh_token"),
                ((Number) response.get("expires_in")).longValue(),
                ((Number) response.get("refresh_expires_in")).longValue(),
                (String) response.get("token_type")
        );
    }
}

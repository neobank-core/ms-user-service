package org.neobank.userservice.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neobank.userservice.dto.RegisterUserRequest;
import org.neobank.userservice.entity.User;
import org.neobank.userservice.repository.UserRepository;
import org.neobank.userservice.service.KeycloakUserService;
import org.neobank.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserService userService;
    private final UserRepository userRepository;
    private final KeycloakUserService keycloakUserService;

    @Value("${keycloak.auth-server-url:http://localhost:8090}")
    private String keycloakUrl;

    @Value("${keycloak.realm:neobank}")
    private String realm;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String API_GATEWAY_URL = "http://localhost:8080";

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting Data Seeder...");

        User admin = setupUser("admin@neobank.com", "admin", "Admin", "NeoBank", "+10000000000");
        User user1 = setupUser("user1@neobank.com", "user1", "User", "One", "+10000000001");
        User user2 = setupUser("user2@neobank.com", "user2", "User", "Two", "+10000000002");

        if (hasCards(user1.getUsername(), "user1")) {
            log.info("Data already seeded. Skipping...");
            return;
        }

        log.info("Seeding accounts, cards and transactions...");

        String token1 = getToken(user1.getUsername(), "user1");
        String token2 = getToken(user2.getUsername(), "user2");

        // 3. Создаем счета
        createAccount(token1, "USD");
        createAccount(token2, "USD");

        // 4. Пополняем счета (Deposit)
        deposit(token1, new BigDecimal("10000.00"), "USD");
        deposit(token2, new BigDecimal("5000.00"), "USD");

        // 5. Выпускаем карты
        UUID card1Id = createCard(token1, "DEBIT", "User One");
        UUID card2Id = createCard(token2, "DEBIT", "User Two");

        // 6. Генерируем 20 транзакций туда-сюда
        for (int i = 0; i < 10; i++) {
            transfer(token1, card1Id, card2Id, new BigDecimal("50.00"), "USD", UUID.randomUUID().toString());
            transfer(token2, card2Id, card1Id, new BigDecimal("25.00"), "USD", UUID.randomUUID().toString());
        }

        log.info("Data Seeding Completed Successfully!");
    }

    private User setupUser(String username, String password, String firstName, String lastName, String phone) {
        String keycloakId = keycloakUserService.getUserIdByUsername(username);
        if (keycloakId == null) {
            log.info("Creating user in Keycloak: {}", username);
            RegisterUserRequest req = new RegisterUserRequest(username, password, username, firstName, lastName, phone);
            keycloakId = keycloakUserService.createUser(req);
        }

        String finalKeycloakId = keycloakId;
        return userRepository.findByKeycloakUserId(keycloakId)
                .orElseGet(() -> {
                    log.info("Creating user in DB: {}", username);
                    User user = User.builder()
                            .username(username)
                            .keycloakUserId(finalKeycloakId)
                            .email(username)
                            .firstName(firstName)
                            .lastName(lastName)
                            .phone(phone)
                            .build();
                    return userRepository.save(user);
                });
    }

    private String getToken(String username, String password) {
        String url = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", "neobank-frontend");
        body.add("username", username);
        body.add("password", password);
        body.add("grant_type", "password");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
        return (String) response.getBody().get("access_token");
    }

    private boolean hasCards(String username, String password) {
        try {
            String token = getToken(username, password);
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            ResponseEntity<List> response = restTemplate.exchange(
                    API_GATEWAY_URL + "/api/cards/my", HttpMethod.GET, new HttpEntity<>(headers), List.class);
            return response.getBody() != null && !response.getBody().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private void createAccount(String token, String currency) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        String body = String.format("{\"currency\": \"%s\"}", currency);
        try {
            restTemplate.postForEntity(API_GATEWAY_URL + "/api/accounts", new HttpEntity<>(body, headers), Map.class);
        } catch (Exception e) {
            log.warn("Failed to create account (might exist): {}", e.getMessage());
        }
    }

    private void deposit(String token, BigDecimal amount, String currency) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        String body = String.format("{\"amount\": %s, \"currency\": \"%s\"}", amount, currency);
        restTemplate.postForEntity(API_GATEWAY_URL + "/api/transactions/deposit", new HttpEntity<>(body, headers),
                Map.class);
    }

    private UUID createCard(String token, String type, String name) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Сначала получаем accountId
        ResponseEntity<Map> accResponse = restTemplate.exchange(
                API_GATEWAY_URL + "/api/accounts/my", HttpMethod.GET, new HttpEntity<>(headers), Map.class);
        String accountId = (String) accResponse.getBody().get("id");

        String body = String.format("{\"accountId\": \"%s\", \"cardType\": \"%s\", \"cardHolderName\": \"%s\"}",
                accountId, type, name);
        ResponseEntity<Map> response = restTemplate.postForEntity(API_GATEWAY_URL + "/api/cards",
                new HttpEntity<>(body, headers), Map.class);
        return UUID.fromString((String) response.getBody().get("id"));
    }

    private void transfer(String token, UUID senderCard, UUID receiverCard, BigDecimal amount, String currency,
            String idempotencyKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Idempotency-Key", idempotencyKey);

        String body = String.format(
                "{\"senderCardId\": \"%s\", \"receiverCardId\": \"%s\", \"amount\": %s, \"currency\": \"%s\"}",
                senderCard, receiverCard, amount, currency);
        try {
            restTemplate.postForEntity(API_GATEWAY_URL + "/api/transactions/transfer", new HttpEntity<>(body, headers),
                    Map.class);
        } catch (Exception e) {
            log.error("Transfer failed: {}", e.getMessage());
        }
    }
}

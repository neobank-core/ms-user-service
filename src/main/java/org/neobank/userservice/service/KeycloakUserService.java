    package org.neobank.userservice.service;

    import jakarta.ws.rs.core.Response;
    import lombok.RequiredArgsConstructor;
    import org.keycloak.admin.client.Keycloak;
    import org.keycloak.representations.idm.CredentialRepresentation;
    import org.keycloak.representations.idm.UserRepresentation;
    import org.neobank.userservice.dto.RegisterUserRequest;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.stereotype.Service;

    import java.util.List;

    @Service
    @RequiredArgsConstructor
    public class KeycloakUserService {
        @Value("${keycloak.realm}")
        private String realm;
        private final Keycloak keycloak;

        public String createUser(RegisterUserRequest request) {
            UserRepresentation user = new UserRepresentation();
            user.setUsername(request.username());
            user.setEmail(request.email());
            user.setFirstName(request.firstName());
            user.setLastName(request.lastName());
            user.setEnabled(true);

            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(request.password());
            credential.setTemporary(false);
            user.setCredentials(List.of(credential));
            try (Response response = keycloak.realm(realm).users().create(user)) {
                if (response.getStatus() != 201) {
                    throw new RuntimeException("Failed to create user in Keycloak: " + response.getStatusInfo());
                }
                String[] segments = response.getLocation().getPath().split("/");
                String userId = segments[segments.length - 1];
                var userResource = keycloak.realm(realm).users().get(userId);
                var role = keycloak.realm(realm).roles().get("USER").toRepresentation();
                userResource.roles().realmLevel().add(List.of(role));

                return userId;
            } catch (Exception e) {
                throw new RuntimeException("Error creating user in Keycloak: " + e.getMessage(), e);
            }
        }
    }

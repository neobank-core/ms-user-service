package org.neobank.userservice.mapper;

import org.neobank.userservice.dto.UserResponse;
import org.neobank.userservice.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName()
        );
    }
}

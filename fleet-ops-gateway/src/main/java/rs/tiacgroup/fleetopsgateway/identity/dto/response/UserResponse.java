package rs.tiacgroup.fleetopsgateway.identity.dto.response;

import rs.tiacgroup.fleetopsgateway.identity.entity.UserRole;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        UserRole role,
        boolean active,
        String companyName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
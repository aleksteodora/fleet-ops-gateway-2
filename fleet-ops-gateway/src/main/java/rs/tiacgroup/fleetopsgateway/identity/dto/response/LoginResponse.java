package rs.tiacgroup.fleetopsgateway.identity.dto.response;

import rs.tiacgroup.fleetopsgateway.identity.entity.UserRole;

public record LoginResponse(
        Long userId,
        Long companyId,
        UserRole role
) {
}
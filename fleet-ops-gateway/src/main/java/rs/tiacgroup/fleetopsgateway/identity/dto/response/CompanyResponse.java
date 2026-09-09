package rs.tiacgroup.fleetopsgateway.identity.dto.response;

import java.time.LocalDateTime;

public record CompanyResponse(
        Long id,
        String name,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
package rs.tiacgroup.fleetopsgateway.identity.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import rs.tiacgroup.fleetopsgateway.identity.entity.UserRole;

public record CreateUserRequest(

        Long companyId,

        @NotBlank(message = "Email is required")
        @Size(max = 50, message = "Email must not exceed 50 characters")
        String email,

        @NotBlank(message = "First name is required")
        @Size(max = 30, message = "First name must not exceed 30 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 50, message = "Last name must not exceed 50 characters")
        String lastName,

        @NotNull(message = "Role is required")
        UserRole role

) {
}
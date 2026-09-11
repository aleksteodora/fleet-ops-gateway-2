package rs.tiacgroup.fleetopsgateway.identity.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCompanyRequest(

        @NotBlank(message = "Company name is required")
        @Size(max = 50, message = "Company name must not exceed 50 characters")
        String name

) {
}
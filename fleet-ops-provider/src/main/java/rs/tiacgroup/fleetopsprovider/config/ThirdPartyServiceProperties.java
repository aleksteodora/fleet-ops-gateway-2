package rs.tiacgroup.fleetopsprovider.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "third-party")
@Validated
public record ThirdPartyServiceProperties(

        @NotNull @Valid
        ThirdPartyService free,

        @NotNull @Valid
        ThirdPartyService premium

) {

    public record ThirdPartyService(

            @DecimalMin("0.0")
            @DecimalMax("1.0")
            double failureRate

    ) {
    }
}

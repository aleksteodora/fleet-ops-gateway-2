package rs.tiacgroup.fleetopsprovider.controller;

import rs.tiacgroup.fleetopsprovider.config.ThirdPartyServiceProperties;
import rs.tiacgroup.fleetopsprovider.model.dto.PremiumVehicleDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import rs.tiacgroup.fleetopsprovider.service.PremiumThirdPartyService;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/premium-third-party")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(
        name = "Premium Third Party API",
        description = "Simulates a PREMIUM external vehicle search service with configurable failure rate"
)
public class PremiumThirdPartyController {

    private final PremiumThirdPartyService service;
    private final ThirdPartyServiceProperties properties;

    @GetMapping
    @Operation(
            summary = "Search vehicles (PREMIUM service)",
            description = "Returns enriched vehicle data. This endpoint simulates a more reliable third-party service " +
                    "with a lower failure rate compared to FREE provider.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful response with enriched vehicle data",
                            content = @Content(
                                    array = @ArraySchema(schema = @Schema(implementation = PremiumVehicleDto.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid query parameter",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "503",
                            description = "Simulated premium third-party service unavailable",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<PremiumVehicleDto> search(
            @RequestParam
            @NotBlank
            @Parameter(description = "Search query for vehicle vin", example = "4K7S813551C075029")
            String query
    ) {
        log.debug("PREMIUM third-party request received. query={}", query);
        simulateFailure(query);
        List<PremiumVehicleDto> result = service.search(query);
        log.debug("PREMIUM third-party response size={}", result.size());

        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result.getFirst());
    }

    /**
     * Simulates random failures based on configured failure rate.
     */
    private void simulateFailure(@NotBlank String query) {
        double random = ThreadLocalRandom.current().nextDouble();

        if (shouldFail(random)) {
            String message = String.format(
                    "PREMIUM service unavailable (simulated). query='%s', random=%.4f",
                    query,
                    random
            );

            log.warn(message);

            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    message
            );
        }
    }

    /**
     * Determines whether the request should fail based on random value
     * and configured failure rate.
     */
    private boolean shouldFail(double random) {
        double premiumFailureRate = properties.premium().failureRate();
        return random < premiumFailureRate;
    }
}

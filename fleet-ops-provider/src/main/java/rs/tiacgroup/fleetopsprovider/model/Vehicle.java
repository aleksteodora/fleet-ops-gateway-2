package rs.tiacgroup.fleetopsprovider.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rs.tiacgroup.fleetopsprovider.model.enums.FuelType;
import rs.tiacgroup.fleetopsprovider.model.enums.VehicleStatus;

@Entity
@Table(name = "vehicles")
@Getter
@NoArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    private String vin;

    private String make;

    private String model;

    private Integer modelYear;

    @Enumerated(EnumType.STRING)
    private FuelType fuelType;

    private String engine;

    @Enumerated(EnumType.STRING)
    private VehicleStatus vehicleStatus;

    public Vehicle(
            String vin,
            String make,
            String model,
            Integer modelYear,
            FuelType fuelType,
            String engine,
            VehicleStatus vehicleStatus
    ) {
        this.vin = vin;
        this.make = make;
        this.model = model;
        this.modelYear = modelYear;
        this.fuelType = fuelType;
        this.engine = engine;
        this.vehicleStatus = vehicleStatus;
    }
}

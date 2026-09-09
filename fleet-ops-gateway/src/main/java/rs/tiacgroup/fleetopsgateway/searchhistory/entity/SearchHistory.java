package rs.tiacgroup.fleetopsgateway.searchhistory.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "search_histories", indexes = {
        @Index(name = "idx_search_history_user_id", columnList = "userId"),
        @Index(name = "idx_search_history_company_id", columnList = "companyId"),
        @Index(name = "idx_search_history_provider", columnList = "provider")
})
@Getter
public class SearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long companyId;

    @Column(nullable = false, length = 17)
    private String vin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProviderType provider;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime searchedAt;

    @Setter
    @Column(length = 50)
    private String make;

    @Setter
    @Column(length = 50)
    private String model;

    @Setter
    private Integer modelYear;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private FuelType fuelType;

    @Setter
    @Column(length = 50)
    private String engine;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private VehicleStatus vehicleStatus;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SearchStatus searchStatus;

    protected SearchHistory() {
    }

    public SearchHistory(Long userId, Long companyId, String vin, ProviderType provider, SearchStatus searchStatus) {
        this.userId = userId;
        this.companyId = companyId;
        this.vin = vin;
        this.provider = provider;
        this.searchStatus = searchStatus;
    }

    public SearchHistory(Long userId, Long companyId, String vin, ProviderType provider,
                         String make, String model, Integer modelYear, FuelType fuelType,
                         String engine, VehicleStatus vehicleStatus, SearchStatus searchStatus) {
        this.userId = userId;
        this.companyId = companyId;
        this.vin = vin;
        this.provider = provider;
        this.make = make;
        this.model = model;
        this.modelYear = modelYear;
        this.fuelType = fuelType;
        this.engine = engine;
        this.vehicleStatus = vehicleStatus;
        this.searchStatus = searchStatus;
    }
}
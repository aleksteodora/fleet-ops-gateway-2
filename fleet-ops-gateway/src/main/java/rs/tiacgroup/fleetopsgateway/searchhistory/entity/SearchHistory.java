package rs.tiacgroup.fleetopsgateway.searchhistory.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "search_history", indexes = {
        @Index(name = "idx_search_history_user_id", columnList = "userId"),
        @Index(name = "idx_search_history_company_id", columnList = "companyId"),
        @Index(name = "idx_search_history_provider", columnList = "provider")
})
@Getter
@Setter
@NoArgsConstructor
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

    @Column(nullable = false, updatable = false)
    private LocalDateTime searchedAt;

    @Column(length = 50)
    private String make;

    @Column(length = 50)
    private String model;

    private Integer modelYear;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private FuelType fuelType;

    @Column(length = 50)
    private String engine;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private VehicleStatus vehicleStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SearchStatus searchStatus;

    @PrePersist
    protected void onCreate() {
        this.searchedAt = LocalDateTime.now();
    }
}
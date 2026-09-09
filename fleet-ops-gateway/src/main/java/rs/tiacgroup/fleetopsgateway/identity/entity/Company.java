package rs.tiacgroup.fleetopsgateway.identity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "companies")
@Getter
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Setter
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Setter
    @Column(nullable = false)
    private boolean active = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();

    protected Company() {
    }

    public Company(String name) {
        this.name = name;
    }
}
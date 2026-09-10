package rs.tiacgroup.fleetopsgateway.identity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.tiacgroup.fleetopsgateway.identity.entity.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
}
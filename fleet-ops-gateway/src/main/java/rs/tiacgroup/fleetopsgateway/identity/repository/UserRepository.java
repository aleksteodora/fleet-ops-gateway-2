package rs.tiacgroup.fleetopsgateway.identity.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.tiacgroup.fleetopsgateway.identity.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findByCompanyId(Long companyId, Pageable pageable);

    Optional<User> findByEmail(String email);
}
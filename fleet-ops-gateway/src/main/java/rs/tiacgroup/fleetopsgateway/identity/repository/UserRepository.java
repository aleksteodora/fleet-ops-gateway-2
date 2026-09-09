package rs.tiacgroup.fleetopsgateway.identity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.tiacgroup.fleetopsgateway.identity.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
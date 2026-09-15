package rs.tiacgroup.fleetopsgateway.identity.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rs.tiacgroup.fleetopsgateway.identity.dto.request.LoginRequest;
import rs.tiacgroup.fleetopsgateway.identity.dto.response.LoginResponse;
import rs.tiacgroup.fleetopsgateway.identity.entity.User;
import rs.tiacgroup.fleetopsgateway.identity.entity.UserRole;
import rs.tiacgroup.fleetopsgateway.identity.exception.InvalidCredentialsException;
import rs.tiacgroup.fleetopsgateway.identity.repository.UserRepository;

import java.util.Optional;

@Service
@Slf4j
public class AuthService {

    private static final String ADMIN_PASSWORD = "Admin1234";

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for email={}", request.email());

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (user.getRole() == UserRole.ADMIN) {
            if (!ADMIN_PASSWORD.equals(request.password())) {
                log.warn("Login failed for admin email={}: wrong password", request.email());
                throw new InvalidCredentialsException("Invalid email or password");
            }
        } else {
            String companyName = user.getCompany() != null ? user.getCompany().getName() : null;
            if (companyName == null || !companyName.equals(request.password())) {
                log.warn("Login failed for user email={}: wrong password", request.email());
                throw new InvalidCredentialsException("Invalid email or password");
            }
        }

        Long companyId = user.getCompany() != null ? user.getCompany().getId() : null;
        log.info("Login successful for email={}", request.email());
        return new LoginResponse(user.getId(), companyId, user.getRole());
    }
}
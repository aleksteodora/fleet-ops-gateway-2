package rs.tiacgroup.fleetopsgateway.identity.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.tiacgroup.fleetopsgateway.identity.dto.UserMapper;
import rs.tiacgroup.fleetopsgateway.identity.dto.request.CreateUserRequest;
import rs.tiacgroup.fleetopsgateway.identity.dto.request.UpdateUserRequest;
import rs.tiacgroup.fleetopsgateway.identity.dto.response.UserResponse;
import rs.tiacgroup.fleetopsgateway.identity.entity.Company;
import rs.tiacgroup.fleetopsgateway.identity.entity.User;
import rs.tiacgroup.fleetopsgateway.identity.entity.UserRole;
import rs.tiacgroup.fleetopsgateway.identity.exception.CompanyNotFoundException;
import rs.tiacgroup.fleetopsgateway.identity.exception.InvalidUserCompanyAssignmentException;
import rs.tiacgroup.fleetopsgateway.identity.exception.UserAlreadyExistsException;
import rs.tiacgroup.fleetopsgateway.identity.exception.UserNotFoundException;
import rs.tiacgroup.fleetopsgateway.identity.repository.CompanyRepository;
import rs.tiacgroup.fleetopsgateway.identity.repository.UserRepository;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, CompanyRepository companyRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.userMapper = userMapper;
    }

    public Page<UserResponse> listUsers(Pageable pageable) {
        log.debug("Fetching users page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        return userRepository.findAll(pageable)
                .map(userMapper::toResponse);
    }

    public UserResponse getUserById(Long id) {
        log.debug("Fetching user with id={}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        log.info("Creating user with email={}", request.email());

        validateCompanyAssignment(request);

        User user = userMapper.toEntity(request);

        if (request.companyId() != null) {
            Company company = companyRepository.findById(request.companyId())
                    .orElseThrow(() -> new CompanyNotFoundException(
                            "Company with id " + request.companyId() + " not found"));
            user.setCompany(company);
        }

        User saved;
        try {
            saved = userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException ex) {
            log.warn("User creation failed, email already exists: {}", request.email());
            throw new UserAlreadyExistsException(
                    "User with email '" + request.email() + "' already exists");
        }

        log.info("User created successfully, id={}", saved.getId());
        return userMapper.toResponse(saved);
    }

    private void validateCompanyAssignment(CreateUserRequest request) {
        if (request.role() == UserRole.ADMIN && request.companyId() != null) {
            throw new InvalidUserCompanyAssignmentException(
                    "Admin users must not be assigned to a company");
        }
        if (request.role() == UserRole.COMPANY_USER && request.companyId() == null) {
            throw new InvalidUserCompanyAssignmentException(
                    "Company users must be assigned to a company");
        }
    }

    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        log.info("Updating user id={} with firstName={} lastName={}", id, request.firstName(), request.lastName());

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));

        userMapper.updateEntityFromRequest(request, user);
        User saved = userRepository.save(user);

        log.info("User updated successfully, id={}", saved.getId());
        return userMapper.toResponse(saved);
    }

    public void deactivateUser(Long id) {
        log.info("Deactivating user id={}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));

        user.setActive(false);
        userRepository.save(user);

        log.info("User deactivated successfully, id={}", id);
    }
}
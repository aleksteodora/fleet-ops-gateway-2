package rs.tiacgroup.fleetopsgateway.identity.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import rs.tiacgroup.fleetopsgateway.identity.dto.UserMapper;
import rs.tiacgroup.fleetopsgateway.identity.dto.request.CreateUserRequest;
import rs.tiacgroup.fleetopsgateway.identity.dto.response.UserResponse;
import rs.tiacgroup.fleetopsgateway.identity.entity.Company;
import rs.tiacgroup.fleetopsgateway.identity.entity.User;
import rs.tiacgroup.fleetopsgateway.identity.entity.UserRole;
import rs.tiacgroup.fleetopsgateway.identity.exception.CompanyNotFoundException;
import rs.tiacgroup.fleetopsgateway.identity.exception.InvalidUserCompanyAssignmentException;
import rs.tiacgroup.fleetopsgateway.identity.exception.UserAlreadyExistsException;
import rs.tiacgroup.fleetopsgateway.identity.repository.CompanyRepository;
import rs.tiacgroup.fleetopsgateway.identity.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final LocalDateTime FIXED_TIMESTAMP = LocalDateTime.of(2026, 1, 1, 12, 0);

    @Mock
    private UserRepository userRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void listUsers_shouldReturnMappedPage() {
        // given
        User user = new User("marko@example.com", "Marko", "Petrovic", UserRole.COMPANY_USER);
        UserResponse response = new UserResponse(
                1L, "marko@example.com", "Marko", "Petrovic", UserRole.COMPANY_USER,
                true, "Test Company", FIXED_TIMESTAMP, FIXED_TIMESTAMP);
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(user), pageable, 1);

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toResponse(user)).thenReturn(response);

        // when
        Page<UserResponse> result = userService.listUsers(pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(response);
        verify(userRepository).findAll(pageable);
        verify(userMapper).toResponse(user);
    }

    @Test
    void createUser_shouldSaveAndReturnMappedResponseWhenCompanyUserWithValidCompany() {
        // given
        CreateUserRequest request = new CreateUserRequest(
                1L, "marko@example.com", "Marko", "Petrovic", UserRole.COMPANY_USER);
        Company company = new Company("Test Company");
        User mappedUser = new User("marko@example.com", "Marko", "Petrovic", UserRole.COMPANY_USER);
        User savedUser = new User("marko@example.com", "Marko", "Petrovic", UserRole.COMPANY_USER);
        UserResponse expectedResponse = new UserResponse(
                1L, "marko@example.com", "Marko", "Petrovic", UserRole.COMPANY_USER,
                true, "Test Company", FIXED_TIMESTAMP, FIXED_TIMESTAMP);

        when(userMapper.toEntity(request)).thenReturn(mappedUser);
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(userRepository.saveAndFlush(mappedUser)).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(expectedResponse);

        // when
        UserResponse result = userService.createUser(request);

        // then
        assertThat(result).isEqualTo(expectedResponse);
        assertThat(mappedUser.getCompany()).isEqualTo(company);
        verify(userRepository).saveAndFlush(mappedUser);
    }

    @Test
    void createUser_shouldSaveAndReturnMappedResponseWhenAdminWithoutCompany() {
        // given
        CreateUserRequest request = new CreateUserRequest(
                null, "admin@example.com", "Ana", "Jovanovic", UserRole.ADMIN);
        User mappedUser = new User("admin@example.com", "Ana", "Jovanovic", UserRole.ADMIN);
        User savedUser = new User("admin@example.com", "Ana", "Jovanovic", UserRole.ADMIN);
        UserResponse expectedResponse = new UserResponse(
                1L, "admin@example.com", "Ana", "Jovanovic", UserRole.ADMIN,
                true, null, FIXED_TIMESTAMP, FIXED_TIMESTAMP);

        when(userMapper.toEntity(request)).thenReturn(mappedUser);
        when(userRepository.saveAndFlush(mappedUser)).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(expectedResponse);

        // when
        UserResponse result = userService.createUser(request);

        // then
        assertThat(result).isEqualTo(expectedResponse);
        verify(companyRepository, never()).findById(any());
        verify(userRepository).saveAndFlush(mappedUser);
    }

    @Test
    void createUser_shouldThrowExceptionWhenAdminHasCompanyId() {
        // given
        CreateUserRequest request = new CreateUserRequest(
                1L, "admin@example.com", "Ana", "Jovanovic", UserRole.ADMIN);

        // when / then
        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(InvalidUserCompanyAssignmentException.class)
                .hasMessageContaining("must not be assigned to a company");

        verify(userMapper, never()).toEntity(any());
        verify(userRepository, never()).saveAndFlush(any());
    }

    @Test
    void createUser_shouldThrowExceptionWhenCompanyUserHasNoCompanyId() {
        // given
        CreateUserRequest request = new CreateUserRequest(
                null, "marko@example.com", "Marko", "Petrovic", UserRole.COMPANY_USER);

        // when / then
        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(InvalidUserCompanyAssignmentException.class)
                .hasMessageContaining("must be assigned to a company");

        verify(userMapper, never()).toEntity(any());
        verify(companyRepository, never()).findById(any());
        verify(userRepository, never()).saveAndFlush(any());
    }

    @Test
    void createUser_shouldThrowExceptionWhenCompanyNotFound() {
        // given
        CreateUserRequest request = new CreateUserRequest(
                999L, "marko@example.com", "Marko", "Petrovic", UserRole.COMPANY_USER);
        User mappedUser = new User("marko@example.com", "Marko", "Petrovic", UserRole.COMPANY_USER);

        when(userMapper.toEntity(request)).thenReturn(mappedUser);
        when(companyRepository.findById(999L)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(CompanyNotFoundException.class)
                .hasMessageContaining("999");

        verify(userRepository, never()).saveAndFlush(any());
    }

    @Test
    void createUser_shouldThrowExceptionWhenEmailAlreadyExists() {
        // given
        CreateUserRequest request = new CreateUserRequest(
                null, "admin@example.com", "Ana", "Jovanovic", UserRole.ADMIN);
        User mappedUser = new User("admin@example.com", "Ana", "Jovanovic", UserRole.ADMIN);

        when(userMapper.toEntity(request)).thenReturn(mappedUser);
        when(userRepository.saveAndFlush(mappedUser))
                .thenThrow(new DataIntegrityViolationException("duplicate key"));

        // when / then
        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("admin@example.com");
    }
}
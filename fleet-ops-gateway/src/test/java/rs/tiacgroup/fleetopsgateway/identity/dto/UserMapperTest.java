package rs.tiacgroup.fleetopsgateway.identity.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import rs.tiacgroup.fleetopsgateway.identity.dto.request.CreateUserRequest;
import rs.tiacgroup.fleetopsgateway.identity.dto.request.UpdateUserRequest;
import rs.tiacgroup.fleetopsgateway.identity.dto.response.UserResponse;
import rs.tiacgroup.fleetopsgateway.identity.entity.Company;
import rs.tiacgroup.fleetopsgateway.identity.entity.User;
import rs.tiacgroup.fleetopsgateway.identity.entity.UserRole;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = Mappers.getMapper(UserMapper.class);
    }

    @Test
    void toResponse_shouldMapAllFieldsCorrectlyWhenCompanyPresent() {
        // given
        Company company = new Company("Test Company");
        User user = new User("marko@example.com", "Marko", "Petrovic", UserRole.COMPANY_USER);
        user.setCompany(company);

        UserResponse expected = new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.isActive(),
                company.getName(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );

        // when
        UserResponse actual = userMapper.toResponse(user);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void toResponse_shouldMapAllFieldsCorrectlyWhenCompanyIsNull() {
        // given
        User admin = new User("admin@example.com", "Ana", "Jovanovic", UserRole.ADMIN);

        UserResponse expected = new UserResponse(
                admin.getId(),
                admin.getEmail(),
                admin.getFirstName(),
                admin.getLastName(),
                admin.getRole(),
                admin.isActive(),
                null,
                admin.getCreatedAt(),
                admin.getUpdatedAt()
        );

        // when
        UserResponse actual = userMapper.toResponse(admin);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void toEntity_shouldMapFieldsFromRequestAndIgnoreCompany() {
        // given
        CreateUserRequest request = new CreateUserRequest(
                1L, "marko@example.com", "Marko", "Petrovic", UserRole.COMPANY_USER);

        // when
        User actual = userMapper.toEntity(request);

        // then
        assertThat(actual.getEmail()).isEqualTo("marko@example.com");
        assertThat(actual.getFirstName()).isEqualTo("Marko");
        assertThat(actual.getLastName()).isEqualTo("Petrovic");
        assertThat(actual.getRole()).isEqualTo(UserRole.COMPANY_USER);
        assertThat(actual.getCompany()).isNull();
    }

    @Test
    void updateEntityFromRequest_shouldUpdateFirstNameAndLastNameOnExistingUser() {
        // given
        User user = new User("marko@example.com", "OldFirst", "OldLast", UserRole.COMPANY_USER);
        UpdateUserRequest request = new UpdateUserRequest("NewFirst", "NewLast");

        // when
        userMapper.updateEntityFromRequest(request, user);

        // then
        assertThat(user.getFirstName()).isEqualTo("NewFirst");
        assertThat(user.getLastName()).isEqualTo("NewLast");
    }
}
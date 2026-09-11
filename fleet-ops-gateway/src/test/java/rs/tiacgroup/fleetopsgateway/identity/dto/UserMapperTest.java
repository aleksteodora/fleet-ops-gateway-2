package rs.tiacgroup.fleetopsgateway.identity.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
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

        // when
        UserResponse actual = userMapper.toResponse(user);

        // then
        assertThat(actual.id()).isEqualTo(user.getId());
        assertThat(actual.email()).isEqualTo(user.getEmail());
        assertThat(actual.firstName()).isEqualTo(user.getFirstName());
        assertThat(actual.lastName()).isEqualTo(user.getLastName());
        assertThat(actual.role()).isEqualTo(user.getRole());
        assertThat(actual.active()).isEqualTo(user.isActive());
        assertThat(actual.companyName()).isEqualTo("Test Company");
        assertThat(actual.createdAt()).isEqualTo(user.getCreatedAt());
        assertThat(actual.updatedAt()).isEqualTo(user.getUpdatedAt());
    }

    @Test
    void toResponse_shouldMapCompanyNameAsNullWhenCompanyIsNull() {
        // given
        User admin = new User("admin@example.com", "Ana", "Jovanovic", UserRole.ADMIN);

        // when
        UserResponse actual = userMapper.toResponse(admin);

        // then
        assertThat(actual.companyName()).isNull();
    }
}
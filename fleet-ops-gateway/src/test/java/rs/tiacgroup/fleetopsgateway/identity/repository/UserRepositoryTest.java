package rs.tiacgroup.fleetopsgateway.identity.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import rs.tiacgroup.fleetopsgateway.identity.entity.Company;
import rs.tiacgroup.fleetopsgateway.identity.entity.User;
import rs.tiacgroup.fleetopsgateway.identity.entity.UserRole;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void findAll_shouldReturnPagedUsersInCorrectOrder() {
        // given
        User alice = userRepository.save(new User("alice@example.com", "Alice", "Anderson", UserRole.COMPANY_USER));
        User bob = userRepository.save(new User("bob@example.com", "Bob", "Brown", UserRole.COMPANY_USER));
        userRepository.save(new User("carol@example.com", "Carol", "Clark", UserRole.COMPANY_USER));

        // when
        Page<User> result = userRepository.findAll(PageRequest.of(0, 2, Sort.by("id")));

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getContent().get(0).getId()).isEqualTo(alice.getId());
        assertThat(result.getContent().get(1).getId()).isEqualTo(bob.getId());
    }

    @Test
    void save_shouldRejectDuplicateEmail() {
        // given
        userRepository.saveAndFlush(new User("duplicate@example.com", "First", "User", UserRole.ADMIN));
        User duplicate = new User("duplicate@example.com", "Second", "User", UserRole.ADMIN);

        // when / then
        assertThatThrownBy(() -> userRepository.saveAndFlush(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void findByCompanyId_shouldReturnOnlyUsersFromThatCompany() {
        // given
        Company company = companyRepository.saveAndFlush(new Company("Test Company"));
        Company otherCompany = companyRepository.saveAndFlush(new Company("Other Company"));

        User user1 = new User("user1@example.com", "First", "User", UserRole.COMPANY_USER);
        user1.setCompany(company);
        userRepository.save(user1);

        User user2 = new User("user2@example.com", "Second", "User", UserRole.COMPANY_USER);
        user2.setCompany(otherCompany);
        userRepository.save(user2);

        // when
        Page<User> result = userRepository.findByCompanyId(company.getId(), PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getEmail()).isEqualTo("user1@example.com");
    }
}
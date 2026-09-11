package rs.tiacgroup.fleetopsgateway.identity.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import rs.tiacgroup.fleetopsgateway.identity.entity.User;
import rs.tiacgroup.fleetopsgateway.identity.entity.UserRole;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

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
}
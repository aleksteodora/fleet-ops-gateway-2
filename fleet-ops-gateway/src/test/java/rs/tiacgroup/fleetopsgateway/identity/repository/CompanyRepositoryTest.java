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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
class CompanyRepositoryTest {

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void save_shouldPersistCompanyAndGenerateIdAndTimestamps() {
        // given
        Company company = new Company("Test Company");

        // when
        Company saved = companyRepository.saveAndFlush(company);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Test Company");
        assertThat(saved.isActive()).isTrue();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void findAll_shouldReturnPagedCompaniesInCorrectOrder() {
        // given
        Company alpha = companyRepository.save(new Company("Alpha Logistics"));
        Company beta = companyRepository.save(new Company("Beta Transport"));
        companyRepository.save(new Company("Gamma Fleet"));

        // when
        Page<Company> result = companyRepository.findAll(PageRequest.of(0, 2, Sort.by("id")));

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getContent().get(0).getId()).isEqualTo(alpha.getId());
        assertThat(result.getContent().get(1).getId()).isEqualTo(beta.getId());
    }

    @Test
    void save_shouldRejectDuplicateCompanyName() {
        // given
        companyRepository.saveAndFlush(new Company("Duplicate Company"));
        Company duplicate = new Company("Duplicate Company");

        // when / then
        assertThatThrownBy(() -> companyRepository.saveAndFlush(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void existsByName_shouldReturnTrueWhenCompanyExists() {
        // given
        companyRepository.saveAndFlush(new Company("Existing Company"));

        // when
        boolean exists = companyRepository.existsByName("Existing Company");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void existsByName_shouldReturnFalseWhenCompanyDoesNotExist() {
        // when
        boolean exists = companyRepository.existsByName("Nonexistent Company");

        // then
        assertThat(exists).isFalse();
    }
}
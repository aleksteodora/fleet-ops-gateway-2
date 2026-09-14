package rs.tiacgroup.fleetopsgateway.identity.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import rs.tiacgroup.fleetopsgateway.identity.dto.CompanyMapper;
import rs.tiacgroup.fleetopsgateway.identity.dto.UserMapper;
import rs.tiacgroup.fleetopsgateway.identity.dto.request.CreateCompanyRequest;
import rs.tiacgroup.fleetopsgateway.identity.dto.request.UpdateCompanyRequest;
import rs.tiacgroup.fleetopsgateway.identity.dto.response.CompanyResponse;
import rs.tiacgroup.fleetopsgateway.identity.dto.response.UserResponse;
import rs.tiacgroup.fleetopsgateway.identity.entity.Company;
import rs.tiacgroup.fleetopsgateway.identity.entity.User;
import rs.tiacgroup.fleetopsgateway.identity.entity.UserRole;
import rs.tiacgroup.fleetopsgateway.identity.exception.CompanyAlreadyExistsException;
import rs.tiacgroup.fleetopsgateway.identity.exception.CompanyNotFoundException;
import rs.tiacgroup.fleetopsgateway.identity.repository.CompanyRepository;
import rs.tiacgroup.fleetopsgateway.identity.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    private static final LocalDateTime FIXED_TIMESTAMP = LocalDateTime.of(2026, 1, 1, 12, 0);
    private static final String TEST_COMPANY_NAME = "Test Company";
    private static final String NEW_COMPANY_NAME = "New Company";
    private static final String OLD_COMPANY_NAME = "Old Name";
    private static final String COMPANY_USER_EMAIL = "marko@example.com";
    private static final String COMPANY_USER_FIRST_NAME = "Marko";
    private static final String COMPANY_USER_LAST_NAME = "Petrovic";

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyMapper companyMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private CompanyService companyService;

    @Test
    void listCompanies_shouldReturnMappedPage() {
        // given
        Company company = new Company(TEST_COMPANY_NAME);
        CompanyResponse response = new CompanyResponse(
                1L, TEST_COMPANY_NAME, true, FIXED_TIMESTAMP, FIXED_TIMESTAMP);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Company> companyPage = new PageImpl<>(List.of(company), pageable, 1);

        when(companyRepository.findAll(pageable)).thenReturn(companyPage);
        when(companyMapper.toResponse(company)).thenReturn(response);

        // when
        Page<CompanyResponse> result = companyService.listCompanies(pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(response);
        verify(companyRepository).findAll(pageable);
        verify(companyMapper).toResponse(company);
    }

    @Test
    void createCompany_shouldSaveAndReturnMappedResponse() {
        // given
        CreateCompanyRequest request = new CreateCompanyRequest(NEW_COMPANY_NAME);
        Company mappedCompany = new Company(NEW_COMPANY_NAME);
        Company savedCompany = new Company(NEW_COMPANY_NAME);
        CompanyResponse expectedResponse = new CompanyResponse(
                1L, NEW_COMPANY_NAME, true, FIXED_TIMESTAMP, FIXED_TIMESTAMP);

        when(companyMapper.toEntity(request)).thenReturn(mappedCompany);
        when(companyRepository.saveAndFlush(mappedCompany)).thenReturn(savedCompany);
        when(companyMapper.toResponse(savedCompany)).thenReturn(expectedResponse);

        // when
        CompanyResponse result = companyService.createCompany(request);

        // then
        assertThat(result).isEqualTo(expectedResponse);
        verify(companyRepository).saveAndFlush(mappedCompany);
    }

    @Test
    void createCompany_shouldSetActiveTrueBeforeSaving() {
        // given
        CreateCompanyRequest request = new CreateCompanyRequest(NEW_COMPANY_NAME);
        Company mappedCompany = new Company(NEW_COMPANY_NAME);
        Company savedCompany = new Company(NEW_COMPANY_NAME);

        when(companyMapper.toEntity(request)).thenReturn(mappedCompany);
        when(companyRepository.saveAndFlush(any(Company.class))).thenReturn(savedCompany);
        when(companyMapper.toResponse(savedCompany)).thenReturn(
                new CompanyResponse(1L, NEW_COMPANY_NAME, true, FIXED_TIMESTAMP, FIXED_TIMESTAMP));

        ArgumentCaptor<Company> companyCaptor = ArgumentCaptor.forClass(Company.class);

        // when
        companyService.createCompany(request);

        // then
        verify(companyRepository).saveAndFlush(companyCaptor.capture());
        assertThat(companyCaptor.getValue().isActive()).isTrue();
    }

    @Test
    void createCompany_shouldThrowExceptionWhenNameAlreadyExists() {
        // given
        CreateCompanyRequest request = new CreateCompanyRequest("Existing Company");
        Company mappedCompany = new Company("Existing Company");

        when(companyMapper.toEntity(request)).thenReturn(mappedCompany);
        when(companyRepository.saveAndFlush(mappedCompany))
                .thenThrow(new DataIntegrityViolationException("duplicate key"));

        // when / then
        assertThatThrownBy(() -> companyService.createCompany(request))
                .isInstanceOf(CompanyAlreadyExistsException.class)
                .hasMessageContaining("Existing Company");
    }

    @Test
    void getCompanyById_shouldReturnMappedResponseWhenFound() {
        // given
        Long id = 1L;
        Company company = new Company(TEST_COMPANY_NAME);
        CompanyResponse response = new CompanyResponse(
                id, TEST_COMPANY_NAME, true, FIXED_TIMESTAMP, FIXED_TIMESTAMP);

        when(companyRepository.findById(id)).thenReturn(Optional.of(company));
        when(companyMapper.toResponse(company)).thenReturn(response);

        // when
        CompanyResponse result = companyService.getCompanyById(id);

        // then
        assertThat(result).isEqualTo(response);
        verify(companyRepository).findById(id);
        verify(companyMapper).toResponse(company);
    }

    @Test
    void getCompanyById_shouldThrowExceptionWhenNotFound() {
        // given
        Long id = 999L;
        when(companyRepository.findById(id)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> companyService.getCompanyById(id))
                .isInstanceOf(CompanyNotFoundException.class)
                .hasMessageContaining("999");

        verify(companyMapper, never()).toResponse(any());
    }

    @Test
    void updateCompany_shouldUpdateAndReturnMappedResponse() {
        // given
        Long id = 1L;
        UpdateCompanyRequest request = new UpdateCompanyRequest("Updated Name");
        Company existingCompany = new Company(OLD_COMPANY_NAME);
        CompanyResponse expectedResponse = new CompanyResponse(
                id, "Updated Name", true, FIXED_TIMESTAMP, FIXED_TIMESTAMP);

        when(companyRepository.findById(id)).thenReturn(Optional.of(existingCompany));
        when(companyRepository.saveAndFlush(existingCompany)).thenReturn(existingCompany);
        when(companyMapper.toResponse(existingCompany)).thenReturn(expectedResponse);

        // when
        CompanyResponse result = companyService.updateCompany(id, request);

        // then
        assertThat(result).isEqualTo(expectedResponse);
        verify(companyMapper).updateEntityFromRequest(request, existingCompany);
        verify(companyRepository).saveAndFlush(existingCompany);
    }

    @Test
    void updateCompany_shouldThrowExceptionWhenCompanyNotFound() {
        // given
        Long id = 999L;
        UpdateCompanyRequest request = new UpdateCompanyRequest("New Name");
        when(companyRepository.findById(id)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> companyService.updateCompany(id, request))
                .isInstanceOf(CompanyNotFoundException.class)
                .hasMessageContaining("999");

        verify(companyMapper, never()).updateEntityFromRequest(any(), any());
        verify(companyRepository, never()).saveAndFlush(any());
    }

    @Test
    void updateCompany_shouldThrowExceptionWhenNewNameAlreadyExists() {
        // given
        Long id = 1L;
        UpdateCompanyRequest request = new UpdateCompanyRequest("Taken Name");
        Company existingCompany = new Company(OLD_COMPANY_NAME);

        when(companyRepository.findById(id)).thenReturn(Optional.of(existingCompany));
        when(companyRepository.saveAndFlush(existingCompany))
                .thenThrow(new DataIntegrityViolationException("duplicate key"));

        // when / then
        assertThatThrownBy(() -> companyService.updateCompany(id, request))
                .isInstanceOf(CompanyAlreadyExistsException.class)
                .hasMessageContaining("Taken Name");
    }

    @Test
    void deactivateCompany_shouldSetActiveFalseAndSave() {
        // given
        Long id = 1L;
        Company company = new Company(TEST_COMPANY_NAME);
        when(companyRepository.findById(id)).thenReturn(Optional.of(company));

        // when
        companyService.deactivateCompany(id);

        // then
        assertThat(company.isActive()).isFalse();
        verify(companyRepository).save(company);
    }

    @Test
    void deactivateCompany_shouldThrowExceptionWhenNotFound() {
        // given
        Long id = 999L;
        when(companyRepository.findById(id)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> companyService.deactivateCompany(id))
                .isInstanceOf(CompanyNotFoundException.class)
                .hasMessageContaining("999");

        verify(companyRepository, never()).save(any());
    }

    @Test
    void getUsersByCompany_shouldReturnMappedPageWhenCompanyExists() {
        // given
        Long companyId = 1L;
        User user = new User(COMPANY_USER_EMAIL, COMPANY_USER_FIRST_NAME, COMPANY_USER_LAST_NAME, UserRole.COMPANY_USER);
        UserResponse response = new UserResponse(
                1L, COMPANY_USER_EMAIL, COMPANY_USER_FIRST_NAME, COMPANY_USER_LAST_NAME, UserRole.COMPANY_USER,
                true, TEST_COMPANY_NAME, FIXED_TIMESTAMP, FIXED_TIMESTAMP);
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(user), pageable, 1);

        when(companyRepository.existsById(companyId)).thenReturn(true);
        when(userRepository.findByCompanyId(companyId, pageable)).thenReturn(userPage);
        when(userMapper.toResponse(user)).thenReturn(response);

        // when
        Page<UserResponse> result = companyService.getUsersByCompany(companyId, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(response);
        verify(userRepository).findByCompanyId(companyId, pageable);
        verify(userMapper).toResponse(user);
    }

    @Test
    void getUsersByCompany_shouldThrowExceptionWhenCompanyNotFound() {
        // given
        Long companyId = 999L;
        Pageable pageable = PageRequest.of(0, 10);
        when(companyRepository.existsById(companyId)).thenReturn(false);

        // when / then
        assertThatThrownBy(() -> companyService.getUsersByCompany(companyId, pageable))
                .isInstanceOf(CompanyNotFoundException.class)
                .hasMessageContaining("999");

        verify(userRepository, never()).findByCompanyId(any(), any());
    }
}
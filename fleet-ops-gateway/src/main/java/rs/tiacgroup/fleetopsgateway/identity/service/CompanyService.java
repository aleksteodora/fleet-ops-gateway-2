package rs.tiacgroup.fleetopsgateway.identity.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.tiacgroup.fleetopsgateway.identity.dto.CompanyMapper;
import rs.tiacgroup.fleetopsgateway.identity.dto.request.CreateCompanyRequest;
import rs.tiacgroup.fleetopsgateway.identity.dto.response.CompanyResponse;
import rs.tiacgroup.fleetopsgateway.identity.entity.Company;
import rs.tiacgroup.fleetopsgateway.identity.exception.CompanyAlreadyExistsException;
import rs.tiacgroup.fleetopsgateway.identity.repository.CompanyRepository;

@Service
@Slf4j
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    public CompanyService(CompanyRepository companyRepository, CompanyMapper companyMapper) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
    }

    public Page<CompanyResponse> listCompanies(Pageable pageable) {
        log.debug("Fetching companies page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        return companyRepository.findAll(pageable)
                .map(companyMapper::toResponse);
    }

    @Transactional
    public CompanyResponse createCompany(CreateCompanyRequest request) {
        log.info("Creating company with name={}", request.name());

        Company company = companyMapper.toEntity(request);
        company.setActive(true);

        Company saved;
        try {
            saved = companyRepository.saveAndFlush(company);
        } catch (DataIntegrityViolationException ex) {
            log.warn("Company creation failed, name already exists: {}", request.name());
            throw new CompanyAlreadyExistsException(
                    "Company with name '" + request.name() + "' already exists");
        }

        log.info("Company created successfully, id={}", saved.getId());
        return companyMapper.toResponse(saved);
    }
}
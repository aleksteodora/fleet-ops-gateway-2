package rs.tiacgroup.fleetopsgateway.identity.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rs.tiacgroup.fleetopsgateway.identity.dto.CompanyMapper;
import rs.tiacgroup.fleetopsgateway.identity.dto.response.CompanyResponse;
import rs.tiacgroup.fleetopsgateway.identity.repository.CompanyRepository;

@Service
public class CompanyService {

    private static final Logger log = LoggerFactory.getLogger(CompanyService.class);

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
}
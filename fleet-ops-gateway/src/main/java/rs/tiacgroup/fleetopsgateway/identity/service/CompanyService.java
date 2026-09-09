package rs.tiacgroup.fleetopsgateway.identity.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rs.tiacgroup.fleetopsgateway.identity.dto.CompanyMapper;
import rs.tiacgroup.fleetopsgateway.identity.dto.response.CompanyResponse;
import rs.tiacgroup.fleetopsgateway.identity.repository.CompanyRepository;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Page<CompanyResponse> listCompanies(Pageable pageable) {
        return companyRepository.findAll(pageable)
                .map(CompanyMapper::toResponse);
    }
}
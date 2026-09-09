package rs.tiacgroup.fleetopsgateway.identity.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.tiacgroup.fleetopsgateway.identity.dto.response.CompanyResponse;
import rs.tiacgroup.fleetopsgateway.identity.service.CompanyService;

@RestController
@RequestMapping("/api/v1/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping
    public Page<CompanyResponse> listCompanies(Pageable pageable) {
        return companyService.listCompanies(pageable);
    }
}
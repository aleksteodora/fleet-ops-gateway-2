package rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection;

import rs.tiacgroup.fleetopsgateway.searchhistory.entity.ProviderType;

public interface CompanyProviderCount {
    Long getCompanyId();
    ProviderType getProvider();
    Long getCount();
}
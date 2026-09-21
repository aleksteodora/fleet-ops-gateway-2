package rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection;

import rs.tiacgroup.fleetopsgateway.searchhistory.entity.SearchStatus;

public interface CompanyOutcomeCount {
    Long getCompanyId();
    SearchStatus getSearchStatus();
    Long getCount();
}
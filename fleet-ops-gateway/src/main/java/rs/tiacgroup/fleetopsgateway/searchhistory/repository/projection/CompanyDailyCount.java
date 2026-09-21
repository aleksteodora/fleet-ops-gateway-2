package rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection;

import java.time.LocalDate;

public interface CompanyDailyCount {
    Long getCompanyId();
    LocalDate getSearchDate();
    Long getCount();
}
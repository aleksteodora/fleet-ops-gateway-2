package rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection;

import java.time.LocalDate;

public interface CompanyWeekBucketCount {
    Long getCompanyId();
    LocalDate getWeekStart();
    Long getCount();
}
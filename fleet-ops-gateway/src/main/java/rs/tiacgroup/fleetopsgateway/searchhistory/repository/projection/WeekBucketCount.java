package rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection;

import java.time.LocalDate;

public interface WeekBucketCount {
    LocalDate getWeekStart();
    Long getCount();
}
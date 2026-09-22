package rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection;

import java.time.LocalDate;

public interface DailyCount {
    LocalDate getSearchDate();
    Long getCount();
}
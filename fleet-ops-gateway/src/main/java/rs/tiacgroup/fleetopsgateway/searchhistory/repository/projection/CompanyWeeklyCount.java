package rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection;

public interface CompanyWeeklyCount {
    Long getCompanyId();
    Integer getYear();
    Integer getWeek();
    Long getCount();
}
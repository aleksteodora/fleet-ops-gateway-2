package rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection;

public interface WeeklyCount {
    Integer getYear();
    Integer getWeek();
    Long getCount();
}
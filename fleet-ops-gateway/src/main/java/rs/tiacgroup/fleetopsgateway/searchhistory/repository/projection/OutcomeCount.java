package rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection;

import rs.tiacgroup.fleetopsgateway.searchhistory.entity.SearchStatus;

public interface OutcomeCount {
    SearchStatus getSearchStatus();
    Long getCount();
}
package rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection;

import rs.tiacgroup.fleetopsgateway.searchhistory.entity.ProviderType;

public interface ProviderCount {
    ProviderType getProvider();
    Long getCount();
}
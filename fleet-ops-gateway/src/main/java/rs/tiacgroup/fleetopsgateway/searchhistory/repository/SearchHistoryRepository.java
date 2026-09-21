package rs.tiacgroup.fleetopsgateway.searchhistory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.SearchHistory;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.CompanyOutcomeCount;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.CompanyProviderCount;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.OutcomeCount;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.ProviderCount;

import java.util.List;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    @Query("SELECT s.provider AS provider, COUNT(s) AS count FROM SearchHistory s GROUP BY s.provider")
    List<ProviderCount> countByProvider();

    @Query("SELECT s.companyId AS companyId, s.provider AS provider, COUNT(s) AS count FROM SearchHistory s GROUP BY s.companyId, s.provider")
    List<CompanyProviderCount> countByCompanyAndProvider();

    @Query("SELECT s.searchStatus AS searchStatus, COUNT(s) AS count FROM SearchHistory s GROUP BY s.searchStatus")
    List<OutcomeCount> countByOutcome();

    @Query("SELECT s.companyId AS companyId, s.searchStatus AS searchStatus, COUNT(s) AS count FROM SearchHistory s GROUP BY s.companyId, s.searchStatus")
    List<CompanyOutcomeCount> countByCompanyAndOutcome();
}
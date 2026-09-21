package rs.tiacgroup.fleetopsgateway.searchhistory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.SearchHistory;

import java.util.List;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    @Query("SELECT s.provider, COUNT(s) FROM SearchHistory s GROUP BY s.provider")
    List<Object[]> countByProvider();

    @Query("SELECT s.companyId, s.provider, COUNT(s) FROM SearchHistory s GROUP BY s.companyId, s.provider")
    List<Object[]> countByCompanyAndProvider();

}
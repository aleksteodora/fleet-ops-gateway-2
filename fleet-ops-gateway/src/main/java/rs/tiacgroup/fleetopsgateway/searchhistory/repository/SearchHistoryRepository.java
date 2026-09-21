package rs.tiacgroup.fleetopsgateway.searchhistory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.SearchHistory;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.*;

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

    @Query(value = "SELECT CAST(searched_at AS DATE) AS searchDate, COUNT(*) AS count " +
            "FROM search_histories GROUP BY CAST(searched_at AS DATE) ORDER BY searchDate",
            nativeQuery = true)
    List<DailyCount> countByDay();

    @Query(value = "SELECT company_id AS companyId, CAST(searched_at AS DATE) AS searchDate, COUNT(*) AS count " +
            "FROM search_histories GROUP BY company_id, CAST(searched_at AS DATE) ORDER BY searchDate",
            nativeQuery = true)
    List<CompanyDailyCount> countByCompanyAndDay();

    @Query(value = "SELECT EXTRACT(YEAR FROM searched_at) AS \"year\", EXTRACT(WEEK FROM searched_at) AS \"week\", COUNT(*) AS count " +
            "FROM search_histories GROUP BY EXTRACT(YEAR FROM searched_at), EXTRACT(WEEK FROM searched_at) ORDER BY \"year\", \"week\"",
            nativeQuery = true)
    List<WeeklyCount> countByWeek();

    @Query(value = "SELECT company_id AS companyId, EXTRACT(YEAR FROM searched_at) AS \"year\", EXTRACT(WEEK FROM searched_at) AS \"week\", COUNT(*) AS count " +
            "FROM search_histories GROUP BY company_id, EXTRACT(YEAR FROM searched_at), EXTRACT(WEEK FROM searched_at) ORDER BY \"year\", \"week\"",
            nativeQuery = true)
    List<CompanyWeeklyCount> countByCompanyAndWeek();
}
package rs.tiacgroup.fleetopsgateway.searchhistory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.SearchHistory;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.CompanyDailyCount;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.CompanyOutcomeCount;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.CompanyProviderCount;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.CompanyWeekBucketCount;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.DailyCount;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.OutcomeCount;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.ProviderCount;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.WeekBucketCount;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    @Query("SELECT s.provider AS provider, COUNT(s) AS count FROM SearchHistory s " +
            "WHERE s.searchedAt >= :from AND s.searchedAt < :to GROUP BY s.provider")
    List<ProviderCount> countByProvider(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT s.companyId AS companyId, s.provider AS provider, COUNT(s) AS count FROM SearchHistory s " +
            "WHERE s.searchedAt >= :from AND s.searchedAt < :to GROUP BY s.companyId, s.provider")
    List<CompanyProviderCount> countByCompanyAndProvider(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT s.searchStatus AS searchStatus, COUNT(s) AS count FROM SearchHistory s " +
            "WHERE s.searchedAt >= :from AND s.searchedAt < :to GROUP BY s.searchStatus")
    List<OutcomeCount> countByOutcome(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT s.companyId AS companyId, s.searchStatus AS searchStatus, COUNT(s) AS count FROM SearchHistory s " +
            "WHERE s.searchedAt >= :from AND s.searchedAt < :to GROUP BY s.companyId, s.searchStatus")
    List<CompanyOutcomeCount> countByCompanyAndOutcome(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query(value = "SELECT CAST(searched_at AS DATE) AS searchDate, COUNT(*) AS count " +
            "FROM search_histories WHERE searched_at >= :from AND searched_at < :to " +
            "GROUP BY CAST(searched_at AS DATE) ORDER BY searchDate",
            nativeQuery = true)
    List<DailyCount> countByDay(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query(value = "SELECT company_id AS companyId, CAST(searched_at AS DATE) AS searchDate, COUNT(*) AS count " +
            "FROM search_histories WHERE searched_at >= :from AND searched_at < :to " +
            "GROUP BY company_id, CAST(searched_at AS DATE) ORDER BY searchDate",
            nativeQuery = true)
    List<CompanyDailyCount> countByCompanyAndDay(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query(value = "SELECT CAST(date_trunc('week', searched_at) AS DATE) AS \"weekStart\", COUNT(*) AS count " +
            "FROM search_histories WHERE searched_at >= :from AND searched_at < :to " +
            "GROUP BY CAST(date_trunc('week', searched_at) AS DATE) ORDER BY \"weekStart\"",
            nativeQuery = true)
    List<WeekBucketCount> countByWeek(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query(value = "SELECT company_id AS companyId, CAST(date_trunc('week', searched_at) AS DATE) AS \"weekStart\", COUNT(*) AS count " +
            "FROM search_histories WHERE searched_at >= :from AND searched_at < :to " +
            "GROUP BY company_id, CAST(date_trunc('week', searched_at) AS DATE) ORDER BY \"weekStart\"",
            nativeQuery = true)
    List<CompanyWeekBucketCount> countByCompanyAndWeek(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
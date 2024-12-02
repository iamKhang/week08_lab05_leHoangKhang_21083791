package vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.models.CandidateApplyJob;
import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.models.Job;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    @Query("SELECT j.type, COUNT(j) as count FROM Job j GROUP BY j.type ORDER BY count DESC")
    List<Object[]> countTopJobTypes();
    @Query("SELECT DISTINCT j FROM Job j " +
            "LEFT JOIN FETCH j.jobSkills js " +
            "LEFT JOIN FETCH js.skill " +
            "WHERE j.active = true")
    List<Job> findAllActiveJobsWithSkills();
    List<Job> findTop4ByActiveOrderByStartDateDesc(boolean active);
    long countByCompanyIdAndActiveTrue(Long companyId);
    @Query("SELECT COUNT(a) FROM CandidateApplyJob a WHERE a.job.company.id = :companyId")
    long countTotalApplicationsByCompanyId(Long companyId);
    @Query("SELECT COUNT(j) FROM Job j WHERE j.company.id = :companyId " +
       "AND j.active = true AND j.deadline <= :expirationDate")
long countExpiringJobs(@Param("companyId") Long companyId, 
                      @Param("expirationDate") LocalDate expirationDate);
    List<Job> findByCompanyId(Long companyId);
    @Query("SELECT a FROM CandidateApplyJob a WHERE a.job.company.id = :companyId " +
           "ORDER BY a.applyDate DESC")
    List<CandidateApplyJob> findRecentApplicationsByCompanyId(Long companyId, Pageable pageable);
    @Query("SELECT a FROM CandidateApplyJob a WHERE a.job.company.id = :companyId " +
           "ORDER BY a.applyDate DESC")
    List<CandidateApplyJob> findAllApplicationsByCompanyId(Long companyId);
    @Query("SELECT caj FROM CandidateApplyJob caj " +
           "JOIN FETCH caj.candidate " +
           "WHERE caj.job.id = :jobId " +
           "ORDER BY caj.applyDate DESC")
    List<CandidateApplyJob> findCandidateApplyJobsByJobId(Long jobId);
}

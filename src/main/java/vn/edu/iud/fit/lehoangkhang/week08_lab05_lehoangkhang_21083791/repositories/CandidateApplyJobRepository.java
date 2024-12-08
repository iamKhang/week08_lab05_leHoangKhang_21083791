package vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.models.Candidate;
import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.models.CandidateApplyJob;
import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.models.Job;

import java.util.Optional;

public interface CandidateApplyJobRepository extends JpaRepository<CandidateApplyJob, Long> {
    boolean existsByCandidateAndJob(Candidate candidate, Job job);
    Optional<CandidateApplyJob> findByCandidateIdAndJobId(Long candidateId, Long jobId);
}

package vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.enums.JobType;
import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.models.CandidateApplyJob;
import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.models.Job;
import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.repositories.JobRepository;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    public Page<Job> getJobs(int page, int size) {
        return jobRepository.findAll(PageRequest.of(page, size));
    }

    public Optional<Job> getJobById(Long id) {
        return jobRepository.findById(id);
    }

    public long countActiveJobsByCompany(Long companyId) {
        return jobRepository.countByCompanyIdAndActiveTrue(companyId);
    }

    public long countTotalApplicationsByCompany(Long companyId) {
        return jobRepository.countTotalApplicationsByCompanyId(companyId);
    }

    public long countExpiringJobsByCompany(Long companyId) {
        LocalDate oneWeekFromNow = LocalDate.now().plusDays(7);
        return jobRepository.countExpiringJobs(companyId, oneWeekFromNow);
    }

    public List<CandidateApplyJob> getRecentApplicationsByCompany(Long companyId) {
        return jobRepository.findRecentApplicationsByCompanyId(companyId, PageRequest.of(0, 5));
    }

    public List<Job> getJobsByCompany(Long companyId) {
        return jobRepository.findByCompanyId(companyId);
    }

    public List<CandidateApplyJob> getAllApplicationsByCompany(Long companyId) {
        return jobRepository.findAllApplicationsByCompanyId(companyId);
    }

    public Job saveJob(Job job) {
        return jobRepository.save(job);
    }

    public List<CandidateApplyJob> getApplicationsByJob(Long jobId) {
        return jobRepository.findCandidateApplyJobsByJobId(jobId);
    }

    public long countTotalViewsByCompany(Long companyId) {
        return jobRepository.countTotalViewsByCompany(companyId);
    }

    public List<String> getAllCities() {
        return jobRepository.findDistinctCities();
    }

    public Page<Job> searchJobs(String keyword, String city, String jobType, 
                              String salaryRange, int page, int size) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            keyword = Arrays.stream(keyword.trim().split("\\s+"))
                .map(term -> "+" + term + "*")
                .collect(Collectors.joining(" "));
        }

        String type = jobType != null && !jobType.isEmpty() ? jobType : null;
        
        Double minSalary = null;
        Double maxSalary = null;
        Boolean negotiable = null;

        if (salaryRange != null && !salaryRange.isEmpty()) {
            if (salaryRange.equals("negotiable")) {
                negotiable = true;
            } else if (salaryRange.equals("20+")) {
                minSalary = 20000000.0;
            } else {
                String[] range = salaryRange.split("-");
                minSalary = Double.parseDouble(range[0]) * 1000000;
                maxSalary = Double.parseDouble(range[1]) * 1000000;
            }
        }

        return jobRepository.searchJobs(
            keyword,
            city,
            type,
            minSalary,
            maxSalary,
            negotiable,
            PageRequest.of(page, size)
        );
    }
}

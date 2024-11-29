package vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

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
}

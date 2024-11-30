package vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.models.Job;
import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.repositories.JobRepository;
import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.services.JobMatchingService;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.models.Candidate;
import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.services.CandidateService;

@Controller
public class HomeController {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobMatchingService jobMatchingService;

    @Autowired
    private CandidateService candidateService;

    @GetMapping("/")
    public String home(Model model, Principal principal) {
        List<Job> recentJobs;
        
        if (principal != null) {
            // Nếu user đã đăng nhập, lấy các công việc phù hợp nhất
            String phone = principal.getName();
            Candidate candidate = candidateService.findByPhone(phone);
            
            if (candidate != null) {
                // Lấy danh sách gợi ý việc làm và sắp xếp theo điểm số
                List<JobMatchingService.JobRecommendation> recommendations = 
                    jobMatchingService.matchCandidateWithJobs(candidate);
                
                // Lấy 4 công việc có điểm cao nhất và mới nhất
                recentJobs = recommendations.stream()
                    .sorted(Comparator
                        .comparing(JobMatchingService.JobRecommendation::getScore).reversed()
                        .thenComparing(r -> r.getJob().getStartDate(), Comparator.reverseOrder()))
                    .limit(4)
                    .map(JobMatchingService.JobRecommendation::getJob)
                    .collect(Collectors.toList());
            } else {
                // Fallback nếu không tìm thấy candidate
                recentJobs = getRecentActiveJobs();
            }
        } else {
            // Nếu chưa đăng nhập, chỉ lấy công việc mới nhất
            recentJobs = getRecentActiveJobs();
        }

        model.addAttribute("recentJobs", recentJobs);
        return "index";
    }

    private List<Job> getRecentActiveJobs() {
        return jobRepository.findTop4ByActiveOrderByStartDateDesc(true);
    }
} 
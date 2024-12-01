package vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        // Kiểm tra nếu user đã đăng nhập
        if (principal != null) {
            // Lấy thông tin authentication
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            
            // Kiểm tra role
            if (auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYER"))) {
                // Nếu là nhà tuyển dụng, chuyển đến dashboard
                return "redirect:/employer/dashboard";
            }
        }

        // Nếu không phải nhà tuyển dụng hoặc chưa đăng nhập, hiển thị trang chủ bình thường
        model.addAttribute("topJobs", jobRepository.findTop4ByActiveOrderByStartDateDesc(true));
        model.addAttribute("jobTypes", jobRepository.countTopJobTypes());
        return "index";
    }
} 
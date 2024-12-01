package vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.models.Account;
import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.models.Company;
import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.models.Job;
import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.services.AccountService;
import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.services.JobService;
import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.services.CompanyService;

import java.security.Principal;

@Controller
@RequestMapping("/employer")
@PreAuthorize("hasRole('EMPLOYER')")
public class EmployerController {

    @Autowired
    private AccountService accountService;
    
    @Autowired
    private JobService jobService;
    
    @Autowired
    private CompanyService companyService;

    @GetMapping("/dashboard")
    public String showDashboard(Model model, Principal principal) {
        // Lấy thông tin account từ email đăng nhập
        Account account = accountService.findByEmail(principal.getName())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        
        Company company = account.getCompany();
        
//        // Thống kê
//        model.addAttribute("activeJobs", jobService.countActiveJobsByCompany(company.getId()));
//        model.addAttribute("totalApplications", jobService.countTotalApplicationsByCompany(company.getId()));
//        model.addAttribute("totalViews", 1000);
//        model.addAttribute("expiringJobs", jobService.countExpiringJobsByCompany(company.getId()));
//
//        // Danh sách ứng viên mới ứng tuyển
//        model.addAttribute("recentApplications",
//            jobService.getRecentApplicationsByCompany(company.getId()));
        
        return "employer/dashboard";
    }

    @GetMapping("/profile")
    public String showProfile(Model model, Principal principal) {
        Account account = accountService.findByEmail(principal.getName())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
            
        model.addAttribute("company", account.getCompany());
        return "employer/profile";
    }

    @GetMapping("/jobs/post")
    public String showPostJobForm(Model model) {
        model.addAttribute("job", new Job());
        return "employer/post-job";
    }

    @GetMapping("/jobs")
    public String listJobs(Model model, Principal principal) {
        Account account = accountService.findByEmail(principal.getName())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
            
        model.addAttribute("jobs", jobService.getJobsByCompany(account.getCompany().getId()));
        return "employer/job-list";
    }

    @GetMapping("/candidates")
    public String listCandidates(Model model, Principal principal) {
        Account account = accountService.findByEmail(principal.getName())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
            
        model.addAttribute("applications", 
            jobService.getAllApplicationsByCompany(account.getCompany().getId()));
        return "employer/candidate-list";
    }
} 
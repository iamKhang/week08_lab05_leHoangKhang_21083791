package vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.controllers;

import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.enums.JobType;
import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.enums.SkillLevel;
import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.models.*;
import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.services.*;

@Controller
@RequestMapping("/employer")
@PreAuthorize("hasRole('EMPLOYER')")
public class EmployerController {
    private final AccountService accountService;
    private final JobService jobService;
    private final CompanyService companyService;
    private final SkillService skillService;
    private final CandidateService candidateService;
    private final JobApplicationService applicationService;

    public EmployerController(AccountService accountService, JobService jobService, 
                            CompanyService companyService, SkillService skillService, CandidateService candidateService, JobApplicationService applicationService) {
        this.accountService = accountService;
        this.jobService = jobService;
        this.companyService = companyService;
        this.skillService = skillService;
        this.candidateService = candidateService;
        this.applicationService = applicationService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, Principal principal) {
        Account account = accountService.findByEmail(principal.getName())
            .orElseThrow(() -> new RuntimeException("Account not found"));
        
        Company company = account.getCompany();
        
        // Thống kê
        long activeJobs = jobService.countActiveJobsByCompany(company.getId());
        long totalApplications = jobService.countTotalApplicationsByCompany(company.getId());
        long expiringJobs = jobService.countExpiringJobsByCompany(company.getId());
        long totalViews = jobService.countTotalViewsByCompany(company.getId());
        
        // Lấy 5 ứng viên mới nhất
        List<CandidateApplyJob> recentApplications = jobService.getRecentApplicationsByCompany(company.getId());
        
        model.addAttribute("activeJobs", activeJobs);
        model.addAttribute("totalApplications", totalApplications);
        model.addAttribute("expiringJobs", expiringJobs);
        model.addAttribute("totalViews", totalViews);
        model.addAttribute("recentApplications", recentApplications);
        
        return "employer/dashboard";
    }

    @GetMapping("/profile")
    public String showProfile(Model model, Principal principal) {
        Account account = accountService.findByEmail(principal.getName())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
            
        model.addAttribute("company", account.getCompany());
        return "employer/profile";
    }
    @PreAuthorize("hasRole('EMPLOYER')")
    @GetMapping("/jobs/post")
    public String showPostJobForm(Model model) {
        model.addAttribute("job", new Job());
        model.addAttribute("jobTypes", JobType.values());
        model.addAttribute("skills", skillService.getAllSkills());
        model.addAttribute("skillLevels", SkillLevel.values());
        return "jobs/add-job";
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

    @PostMapping("/jobs/{id}/toggle-status")
    @ResponseBody
    public ResponseEntity<?> toggleJobStatus(@PathVariable Long id, Principal principal) {
        try {
            Account account = accountService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Account not found"));
            
            Job job = jobService.getJobById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
            
            // Kiểm tra xem job có thuộc về company này không
            if (!job.getCompany().getId().equals(account.getCompany().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            // Toggle trạng thái
            job.setActive(!job.isActive());
            jobService.saveJob(job);
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/jobs/{id}")
    public String getJobDetails(@PathVariable Long id, Model model, Principal principal) {
        Account account = accountService.findByEmail(principal.getName())
            .orElseThrow(() -> new RuntimeException("Account not found"));
        
        Job job = jobService.getJobById(id)
            .orElseThrow(() -> new RuntimeException("Job not found"));
        
        // Kiểm tra quyền truy cập
        if (!job.getCompany().getId().equals(account.getCompany().getId())) {
            return "redirect:/employer/jobs";
        }
        
        // Lấy danh sách ứng viên đã ứng tuyển
        List<CandidateApplyJob> applications = jobService.getApplicationsByJob(id);
        System.out.println("applications: " + applications);
        
        model.addAttribute("job", job);
        model.addAttribute("applications", applications);
        
        return "employer/job-detail";
    }

    @GetMapping("/candidates/{candidateId}/jobs/{jobId}")
    public String viewCandidateDetail(@PathVariable Long candidateId, 
                                    @PathVariable Long jobId,
                                    Model model, 
                                    Principal principal) {
        Account account = accountService.findByEmail(principal.getName())
            .orElseThrow(() -> new RuntimeException("Account not found"));
        
        Candidate candidate = candidateService.findById(candidateId)
            .orElseThrow(() -> new RuntimeException("Candidate not found"));
        
        // Đánh dấu là đã xem
        applicationService.markAsViewedByCompany(candidateId, jobId);
        
        model.addAttribute("candidate", candidate);
        return "employer/candidate-detail";
    }
} 
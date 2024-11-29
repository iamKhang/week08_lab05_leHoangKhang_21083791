package vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.models.Job;
import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.services.JobService;
import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.services.JobApplicationService;
import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.services.CandidateService;

import java.security.Principal;

import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.models.Candidate;

@Controller
@RequestMapping("/jobs")
public class JobController {

    @Autowired
    private JobService jobService;

    @Autowired
    private JobApplicationService applicationService;
    
    @Autowired
    private CandidateService candidateService;

    @GetMapping("")
    public String showJobList(@RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "10") int size,
                               Model model) {
        Page<Job> jobPage = jobService.getJobs(page, size);
        model.addAttribute("jobPage", jobPage);
        return "jobs/job-list";
    }

    @GetMapping("/{id}")
    public String getJobDetails(@PathVariable Long id, Model model, Principal principal) {
        Job job = jobService.getJobById(id)
            .orElseThrow(() -> new RuntimeException("Job not found with id: " + id));
        
        boolean hasApplied = false;
        if (principal != null) {
            String phone = principal.getName();
            Candidate candidate = candidateService.findByPhone(phone);
            hasApplied = applicationService.hasApplied(candidate, id);
        }
        
        model.addAttribute("job", job);
        model.addAttribute("hasApplied", hasApplied);
        return "jobs/job-detail";
    }

    @PostMapping("/{id}/apply")
    public String applyForJob(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            // Lưu URL hiện tại vào session để redirect sau khi đăng nhập
            return "redirect:/login";
        }

        try {
            String phone = principal.getName();
            Candidate candidate = candidateService.findByPhone(phone);
            
            boolean success = applicationService.applyForJob(candidate, id);
            
            if (success) {
                redirectAttributes.addFlashAttribute("successMessage", "Ứng tuyển thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn đã ứng tuyển công việc này rồi!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi ứng tuyển!");
        }
        
        return "redirect:/jobs/" + id;
    }
}

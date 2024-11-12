package vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.controllers;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.enums.SkillLevel;
import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.models.Candidate;
import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.models.CandidateSkill;
import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.models.Experience;
import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.models.Skill;
import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.services.CandidateService;
import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.services.SkillService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/candidates")
public class CandidateController {

    private final CandidateService candidateService;
    private final SkillService skillService;

    @Autowired
    public CandidateController(CandidateService candidateService, SkillService skillService) {
        this.candidateService = candidateService;
        this.skillService = skillService;
    }

    @GetMapping("")
    public String showCandidateList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "30") int size,
            Model model) {
        Page<Candidate> candidatePage = candidateService.getCandidates(page - 1, size);

        model.addAttribute("candidatePage", candidatePage);
        int totalPages = candidatePage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "candidates/candidate-list";
    }

//    @PreAuthorize("hasRole('CANDIDATE')")
    @GetMapping("/add")
    public String showAddCandidateForm(Model model) {
        model.addAttribute("candidate", new Candidate());

        // Lấy danh sách kỹ năng và cấp độ kỹ năng
        List<Skill> skills = skillService.getAllSkills();
        model.addAttribute("skills", skills);
        model.addAttribute("skillLevels", SkillLevel.values());

        return "candidates/add-candidate";
    }

//    @PreAuthorize("hasRole('CANDIDATE')")
    @PostMapping("/add")
    public String addCandidate(@Valid @ModelAttribute("candidate") Candidate candidate, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("skills", skillService.getAllSkills());
            model.addAttribute("skillLevels", SkillLevel.values());
            return "candidates/add-candidate";
        }

        if (candidate.getCandidateSkills() != null) {
            for (CandidateSkill skill : candidate.getCandidateSkills()) {
                skill.setCandidate(candidate);
            }
        }

        if (candidate.getExperiences() != null) {
            for (Experience experience : candidate.getExperiences()) {
                experience.setCandidate(candidate);
            }
        }

        candidateService.saveCandidate(candidate);
        return "redirect:/candidates";
    }
}

package vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.models.*;
import vn.edu.iud.fit.lehoangkhang.week08_lab05_lehoangkhang_21083791.services.*;

import java.util.List;

@Controller
public class SkillRecommendationController {

    @Autowired
    private SkillRecommendationService recommendationService;
    
    @Autowired
    private CandidateService candidateService;

    @GetMapping("/candidates/skill-recommendations")
    public String getSkillRecommendations(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        String phone = userDetails.getUsername();
        Candidate candidate = candidateService.findByPhone(phone);
        
        List<SkillRecommendation> recommendations = 
            recommendationService.getRecommendedSkills(candidate);
            
        model.addAttribute("recommendations", recommendations);
        return "candidates/skill-recommendations";
    }
} 
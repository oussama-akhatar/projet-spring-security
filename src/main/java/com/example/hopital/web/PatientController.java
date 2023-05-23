package com.example.hopital.web;

import com.example.hopital.model.Patient;
import com.example.hopital.repo.PatientRepo;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
public class PatientController {
    private PatientRepo patientRepo;

    @GetMapping("/")
    @PreAuthorize("hasRole('USER')")
    public String root() {
        return "redirect:/user/index";
    }

    @GetMapping("/user/index")
    @PreAuthorize("hasRole('USER')")
    public String index(Model model,
                        @RequestParam(name = "page", defaultValue = "0") int page,
                        @RequestParam(name = "size", defaultValue = "3") int size,
                        @RequestParam(name = "keyword", defaultValue = "") String kw){
        Page<Patient> patientsPage = patientRepo.findByNomContains(kw, PageRequest.of(page,size));
        model.addAttribute("pages", new int[patientsPage.getTotalPages()]);
        model.addAttribute("listPastients", patientsPage.getContent());
        model.addAttribute("keyword", kw);
        model.addAttribute("currentPage", page);
        return "patients";
    }

    @GetMapping("/admin/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(Long id,
                         @RequestParam(name = "keyword", defaultValue = "") String keyword,
                         @RequestParam(name = "page", defaultValue = "0") int page){
        patientRepo.deleteById(id);
        return "redirect:/user/index?page="+page+"&keyword="+keyword;
    }

    @GetMapping("/admin/formPatient")
    @PreAuthorize("hasRole('ADMIN')")
    public String formPatient(Model model){
        model.addAttribute("patient", new Patient());
        return "formPatient";
    }

    @PostMapping("/admin/savePatient")
    @PreAuthorize("hasRole('ADMIN')")
    public String savePatient(@Valid Patient patient, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return "formPatient";
        }
        patientRepo.save(patient);
        return "redirect:/user/index";
    }

    @GetMapping("/admin/editPatient")
    @PreAuthorize("hasRole('ADMIN')")
    public String editPatient(Model model, @RequestParam(name = "id") Long id){
        Patient patient = patientRepo.findById(id).get();
        model.addAttribute("patient", patient);
        return "editPatient";
    }
}
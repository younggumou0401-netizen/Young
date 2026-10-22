package ca.sfu.staffratings.controller;

import ca.sfu.staffratings.model.RoleType;
import ca.sfu.staffratings.model.StaffRating;
import ca.sfu.staffratings.repository.StaffRatingRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class StaffRatingController {
    private final StaffRatingRepository repository;

    public StaffRatingController(StaffRatingRepository repository) { this.repository = repository; }

    @InitBinder("rating")
    void bindEditableFields(WebDataBinder binder) {
        binder.setAllowedFields("name", "email", "roleType", "clarity", "niceness", "knowledgeableScore", "comment");
    }

    @ModelAttribute("roleTypes")
    RoleType[] roleTypes() { return RoleType.values(); }

    @GetMapping("/")
    public String home() { return "redirect:/ratings"; }

    @GetMapping("/ratings")
    public String index(Model model) {
        model.addAttribute("ratings", repository.findAllByOrderByCreatedAtDescIdDesc());
        return "ratings/index";
    }

    @GetMapping("/ratings/new")
    public String newRating(Model model) {
        model.addAttribute("rating", new StaffRating());
        prepareForm(model, null);
        return "ratings/form";
    }

    @PostMapping("/ratings")
    public String create(@Valid @ModelAttribute("rating") StaffRating rating,
                         BindingResult result, Model model, RedirectAttributes flash) {
        if (result.hasErrors()) {
            prepareForm(model, null);
            return "ratings/form";
        }
        StaffRating saved = repository.save(rating);
        flash.addFlashAttribute("success", "Your rating has been added.");
        return "redirect:/ratings/" + saved.getId();
    }

    @GetMapping("/ratings/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("rating", findRating(id));
        return "ratings/detail";
    }

    @GetMapping("/ratings/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("rating", findRating(id));
        prepareForm(model, id);
        return "ratings/form";
    }

    @PostMapping("/ratings/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("rating") StaffRating submitted,
                         BindingResult result, Model model, RedirectAttributes flash) {
        StaffRating existing = findRating(id);
        if (result.hasErrors()) {
            prepareForm(model, id);
            return "ratings/form";
        }
        existing.updateFrom(submitted);
        repository.save(existing);
        flash.addFlashAttribute("success", "Your changes have been saved.");
        return "redirect:/ratings/" + id;
    }

    @GetMapping("/ratings/{id}/delete")
    public String confirmDelete(@PathVariable Long id, Model model) {
        model.addAttribute("rating", findRating(id));
        return "ratings/delete";
    }

    @PostMapping("/ratings/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes flash) {
        repository.delete(findRating(id));
        flash.addFlashAttribute("success", "The rating has been deleted.");
        return "redirect:/ratings";
    }

    @ExceptionHandler(RatingNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String notFound() { return "error/404"; }

    private StaffRating findRating(Long id) {
        return repository.findById(id).orElseThrow(RatingNotFoundException::new);
    }

    private void prepareForm(Model model, Long id) {
        model.addAttribute("editing", id != null);
        model.addAttribute("ratingId", id);
        model.addAttribute("formAction", id == null ? "/ratings" : "/ratings/" + id);
    }

    private static class RatingNotFoundException extends RuntimeException { }
}

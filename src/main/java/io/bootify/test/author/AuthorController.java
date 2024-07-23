package io.bootify.test.author;

import io.bootify.test.util.ReferencedWarning;
import io.bootify.test.util.WebUtils;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/authors")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(final AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String list(final Model model) {
        model.addAttribute("authors", authorService.findAll());
        return "author/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("author") final AuthorDTO authorDTO) {
        return "author/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("author") @Valid final AuthorDTO authorDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "author/add";
        }
        authorService.create(authorDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("author.create.success"));
        return "redirect:/authors";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("author", authorService.get(id));
        return "author/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id,
            @ModelAttribute("author") @Valid final AuthorDTO authorDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "author/edit";
        }
        authorService.update(id, authorDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("author.update.success"));
        return "redirect:/authors";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = authorService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            authorService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("author.delete.success"));
        }
        return "redirect:/authors";
    }

    @GetMapping("/history/{id}")
    public String history(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("revisions", authorService.findRevisions(id));
        return "history/index";
    }

}

package io.bootify.library.envers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@PreAuthorize("hasAuthority('ROLE_USER')")
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "home/index";
    }

}

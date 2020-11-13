package it.minetti;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class MainController {

    @GetMapping(value = "/")
    public ModelAndView method() {
        return new ModelAndView("redirect:/actuator/health/");
    }
}
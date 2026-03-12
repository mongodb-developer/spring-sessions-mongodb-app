package com.mongodb.springsessionsmongodbapp;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/theme")
public class ThemeController {

    @PostMapping
    public Map<String, Object> setTheme(
            @RequestParam String theme,
            HttpSession session) {

        session.setAttribute("theme", theme);

        return Map.of(
                "message", "Theme set",
                "theme", theme,
                "sessionId", session.getId()
        );
    }

    @GetMapping
    public Map<String, Object> getTheme(HttpSession session) {

        String theme = (String) session.getAttribute("theme");

        return Map.of(
                "theme", theme,
                "sessionId", session.getId()
        );
    }
}

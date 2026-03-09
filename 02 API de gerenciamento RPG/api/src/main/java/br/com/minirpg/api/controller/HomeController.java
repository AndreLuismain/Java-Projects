package br.com.minirpg.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "⚔️ API Mini RPG Online! Acesse /swagger-ui/index.html para testar os endpoints.";
    }
}
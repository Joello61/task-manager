package com.example.task_manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Health", description = "Vérification de l'état de l'API")
public class HealthController {

    @GetMapping("/health")
    @Operation(
            summary = "Health check",
            description = "Vérifie que l'API est opérationnelle"
    )
    public String health() {
        return "Ok";
    }

}

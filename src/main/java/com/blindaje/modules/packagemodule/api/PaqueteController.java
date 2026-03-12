package com.blindaje.modules.packagemodule.api;

import com.blindaje.modules.packagemodule.service.PaqueteService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/packages")
public class PaqueteController {

    private final PaqueteService packageService;

    public PaqueteController(PaqueteService packageService) {
        this.packageService = packageService;
    }

    // TODO: Implement package endpoints
}

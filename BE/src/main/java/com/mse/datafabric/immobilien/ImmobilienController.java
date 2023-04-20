package com.mse.datafabric.immobilien;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@ShellComponent
@RestController
@RequestMapping("/api/Gateway")
public class ImmobilienController {

    private final Logger log = LoggerFactory.getLogger( getClass() );
    @Autowired
    ImmobilienRepository immobilienService;

    @ShellMethod( "getImmobilien" )
    @GetMapping("/DataProducts/immobilien")
    public String getImmobilien() throws IOException {
        return immobilienService.getImmobilien();
    }

}


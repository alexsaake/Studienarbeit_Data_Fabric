package com.mse.datafabric.immobilien;

import com.mse.datafabric.immobilien.dtos.ImmobilienBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import java.io.IOException;
import java.util.List;

@ShellComponent
@RestController
@RequestMapping("/api/Gateway")
public class ImmobilienController {

    private final Logger log = LoggerFactory.getLogger( getClass() );
    @Autowired
    ImmobilienRepository immobilienService;

    @ShellMethod( "getImmobilien" )
    @SchemaMapping(typeName = "Query", value = "getImmobilien")
    @GetMapping("/DataProducts/immobilien")
    @PostMapping ("/DataProducts/immobilien")
    public List<ImmobilienBean> getImmobilien() throws IOException {
        return immobilienService.getImmobilien();
    }

}


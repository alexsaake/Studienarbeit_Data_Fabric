package com.mse.datafabric.immobilien;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mse.datafabric.immobilien.dtos.wgSuche.WgSucheAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class ImmobilienTimer {

    @Autowired
    ImmobilienRepository immobilienRepository;
    @Autowired
    WgSucheAdapter wgSucheAdapter;
    @ShellMethod( "getAndSaveFromWgSuche" )
    @Scheduled(cron="0 0 0 * * *") //Mitternacht
    public void getAndSaveFromWgSuche() throws JsonProcessingException {
        immobilienRepository.saveAllNewImmobilien(wgSucheAdapter.getAndMapWgResponseToImmobilienBeanList());
    }
}

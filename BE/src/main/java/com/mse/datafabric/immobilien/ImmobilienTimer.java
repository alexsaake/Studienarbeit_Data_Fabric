package com.mse.datafabric.immobilien;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mse.datafabric.immobilien.dtos.wgGesucht.WgGesuchtAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.stereotype.Component;

@ShellComponent
public class ImmobilienTimer {

    @Autowired
    ImmobilienRepository immobilienRepository;
    @Autowired
    WgGesuchtAdapter wgGesuchtAdapter;
    @ShellMethod( "getAndSaveFromWgGesucht" )
    @Scheduled(cron="0 */5 * * * *") //alle 5 Minuten
    public void getAndSaveFromWgGesucht() throws JsonProcessingException {
        immobilienRepository.saveAllImmobilien(wgGesuchtAdapter.getAndMapWgResponseToImmobilienBeanList()); //wie mit doppelten umgehen? überschreiben?
    }
}

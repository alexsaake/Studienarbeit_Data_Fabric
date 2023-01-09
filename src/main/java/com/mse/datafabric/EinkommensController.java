package com.mse.datafabric;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@RestController
public class EinkommensController {

    @GetMapping("/einkommensentwicklung")
    public String index() throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("Einkommensentwicklung.csv");

        List<EinkommenBean> einkommenBeanList;

        einkommenBeanList = new CsvToBeanBuilder(new InputStreamReader(classPathResource.getInputStream())).withType(EinkommenBean.class).withSeparator(';').withIgnoreQuotations(true).withFieldAsNull(CSVReaderNullFieldIndicator.BOTH).build().parse();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(einkommenBeanList);
    }

}
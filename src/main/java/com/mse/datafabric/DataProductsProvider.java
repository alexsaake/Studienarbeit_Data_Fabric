package com.mse.datafabric;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@Component
class DataProductsProvider implements IDataProductsProvider {
    public List<IDataProductBean> getDataProducts() {
        List<IDataProductBean> dataProducts = new ArrayList<>();

        IDataProductBean dataProduct = new DataProductBean();
        dataProduct.setImage(LoadImage());
        dataProduct.setTitle("Entwicklung der Bruttomonatsverdienste in Deutschland");
        dataProduct.setShortDescription("Entwicklung der durchschnittlichen Bruttomonats-verdienste ab 1991 in Deutschland");
        dataProduct.setLastUpdated(new GregorianCalendar(2022, Calendar.MARCH,24).getTime());
        dataProduct.setAccessRights(DataProductAccessRights.gratis);

        dataProducts.add(dataProduct);

        return dataProducts;
    }

    private byte[] LoadImage() {
        byte[] image = null;
        try {
            File imageResource = new ClassPathResource("BruttomonatsverdiensteDeutschland.png").getFile();
            image = Files.readAllBytes(imageResource.toPath());
        }
        catch (IOException e){
            System.out.println("Could not load image " + e);
        }
        return image;
    }
}

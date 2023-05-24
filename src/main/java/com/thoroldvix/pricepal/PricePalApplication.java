package com.thoroldvix.pricepal;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@ConfigurationPropertiesScan
@Theme(value = "pricepal", variant = Lumo.DARK)
public class PricePalApplication implements AppShellConfigurator {



    public static void main(String[] args) {
        SpringApplication.run(PricePalApplication.class, args);
    }
}
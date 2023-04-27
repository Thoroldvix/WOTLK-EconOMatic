package com.thoroldvix.g2gcalculator;

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
@Theme(value = "g2gcalculator", variant = Lumo.DARK)
public class G2gCalculatorApplication implements AppShellConfigurator {



    public static void main(String[] args) {
        SpringApplication.run(G2gCalculatorApplication.class, args);
    }
}
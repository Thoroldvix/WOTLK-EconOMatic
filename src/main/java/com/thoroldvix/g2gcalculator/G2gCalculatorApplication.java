package com.thoroldvix.g2gcalculator;

import com.thoroldvix.g2gcalculator.price.Price;
import com.thoroldvix.g2gcalculator.price.PriceRepository;
import com.thoroldvix.g2gcalculator.server.ServerResponse;
import com.thoroldvix.g2gcalculator.server.ServerService;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

@SpringBootApplication
@EnableFeignClients
@ConfigurationPropertiesScan
@Theme(value = "g2gcalculator")
public class G2gCalculatorApplication implements AppShellConfigurator {



    public static void main(String[] args) {
        SpringApplication.run(G2gCalculatorApplication.class, args);
    }


}
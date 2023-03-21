package com.example.g2gcalculator;

import com.example.g2gcalculator.config.G2GConfiguration;
import com.example.g2gcalculator.model.Faction;
import com.example.g2gcalculator.model.GameVersion;
import com.example.g2gcalculator.model.Realm;
import com.example.g2gcalculator.model.Region;
import com.example.g2gcalculator.repository.ClassicRealmRepository;
import com.example.g2gcalculator.service.RealmService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@RequiredArgsConstructor
public class G2gCalculatorApplication {

    private final ClassicRealmRepository classicRealmRepository;
    public static void main(String[] args) {
        SpringApplication.run(G2gCalculatorApplication.class, args);
    }

//    @Bean
//    public CommandLineRunner commandLineRunner() {
//        return args -> {
//            Realm realm = Realm.builder()
//                    .name("Cock")
//                    .id(1)
//                    .faction(Faction.ALLIANCE)
//                    .region(Region.builder()
//                            .name("EU")
//                            .build())
//                    .gameVersion(GameVersion.CLASSIC)
//                    .build();
//            classicRealmRepository.save(realm);
//        };
//    }

}
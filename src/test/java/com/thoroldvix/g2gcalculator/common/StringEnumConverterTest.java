package com.thoroldvix.g2gcalculator.common;

import com.thoroldvix.g2gcalculator.server.Faction;
import com.vaadin.flow.router.NotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StringEnumConverterTest {



    @Test
    void fromString_whenCorrectString_thenReturnEnum() {
        Faction faction = StringEnumConverter.fromString("Horde",  Faction.class);
        assertEquals(Faction.HORDE, faction);
    }
    @Test
    void fromString_whenIncorrectString_throwsNotFoundException() {
        assertThrows(NotFoundException.class, () -> StringEnumConverter.fromString("Horde2",  Faction.class));
    }
}
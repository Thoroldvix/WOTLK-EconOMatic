package com.thoroldvix.pricepal.common;

import com.thoroldvix.pricepal.common.error.NotFoundException;
import com.thoroldvix.pricepal.common.util.StringEnumConverter;
import com.thoroldvix.pricepal.server.entity.Faction;
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
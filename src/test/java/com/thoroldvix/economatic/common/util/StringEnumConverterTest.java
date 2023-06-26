package com.thoroldvix.economatic.common.util;

import com.thoroldvix.economatic.shared.StringEnumConverter;
import com.thoroldvix.economatic.error.NotFoundException;
import com.thoroldvix.economatic.server.Faction;
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
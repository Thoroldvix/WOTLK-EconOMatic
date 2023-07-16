package com.thoroldvix.economatic.summary.item;

import com.thoroldvix.economatic.summary.item.ItemSummaryResponse.Quality;
import com.thoroldvix.economatic.summary.item.ItemSummaryResponse.Slot;
import com.thoroldvix.economatic.summary.item.ItemSummaryResponse.Summary;
import com.thoroldvix.economatic.summary.item.ItemSummaryResponse.Type;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemSummaryServiceImplTest {

    @Mock
    private ItemSummaryMapper itemSummaryMapper;
    @Mock
    private ItemSummaryRepository itemSummaryRepository;
    @InjectMocks
    private ItemSummaryServiceImpl underTest;

    @Test
    void getSummary_returnsCorrectItemSummaryResponse() {
        ItemSummaryQualityProjection qualityProjection = getQualityProjection();
        ItemSummaryTypeProjection typeProjection = getTypeProjection();
        ItemSummarySlotProjection slotProjection = getSlotProjection();
        long total = 20000;
        Quality quality = buildQuality();
        Type type = buildType();
        Slot slot = buildSlot();
        Summary summary = new Summary(quality, slot, type, total);
        ItemSummaryResponse expected = new ItemSummaryResponse(summary);

        when(itemSummaryRepository.getQualitySummary()).thenReturn(qualityProjection);
        when(itemSummaryRepository.getTypeSummary()).thenReturn(typeProjection);
        when(itemSummaryRepository.getSlotSummary()).thenReturn(slotProjection);
        when(itemSummaryRepository.count()).thenReturn(total);
        when(itemSummaryMapper.toSummaryResponse(qualityProjection, typeProjection, slotProjection, total))
                .thenReturn(expected);

        ItemSummaryResponse actual = underTest.getSummary();

        assertThat(actual).isEqualTo(expected);
    }

    private static Slot buildSlot() {
        return Slot.builder()
                .nonEquipable(1)
                .head(2)
                .neck(3)
                .shoulder(4)
                .shirt(5)
                .chest(6)
                .waist(7)
                .legs(8)
                .feet(9)
                .wrists(10)
                .hands(11)
                .finger(12)
                .trinket(13)
                .weapon(14)
                .shield(15)
                .ranged(16)
                .back(17)
                .twoHand(18)
                .bag(19)
                .tabard(20)
                .robe(21)
                .mainHand(22)
                .offHand(23)
                .holdable(24)
                .ammo(25)
                .thrown(26)
                .rangedRight(27)
                .quiver(28)
                .relic(29)
                .build();
    }

    private static Quality buildQuality() {
        return Quality.builder()
                .common(1)
                .uncommon(2)
                .rare(3)
                .epic(4)
                .legendary(5)
                .build();
    }

    private static Type buildType() {
        return Type.builder()
                .consumable(1)
                .weapon(2)
                .container(3)
                .gem(4)
                .armor(5)
                .reagent(6)
                .projectile(7)
                .tradeGoods(8)
                .recipe(9)
                .quest(10)
                .key(11)
                .miscellaneous(12)
                .glyph(13)
                .quiver(14)
                .build();
    }

    private ItemSummarySlotProjection getSlotProjection() {
        return new ItemSummarySlotProjection() {
            @Override
            public int getNonEquipable() {
                return 1;
            }

            @Override
            public int getHead() {
                return 2;
            }

            @Override
            public int getNeck() {
                return 3;
            }

            @Override
            public int getShoulder() {
                return 4;
            }

            @Override
            public int getShirt() {
                return 5;
            }

            @Override
            public int getChest() {
                return 6;
            }

            @Override
            public int getWaist() {
                return 7;
            }

            @Override
            public int getLegs() {
                return 8;
            }

            @Override
            public int getFeet() {
                return 9;
            }

            @Override
            public int getWrists() {
                return 10;
            }

            @Override
            public int getHands() {
                return 11;
            }

            @Override
            public int getFinger() {
                return 12;
            }

            @Override
            public int getTrinket() {
                return 13;
            }

            @Override
            public int getWeapon() {
                return 14;
            }

            @Override
            public int getShield() {
                return 15;
            }

            @Override
            public int getRanged() {
                return 16;
            }

            @Override
            public int getBack() {
                return 17;
            }

            @Override
            public int getTwoHand() {
                return 18;
            }

            @Override
            public int getBag() {
                return 19;
            }

            @Override
            public int getTabard() {
                return 20;
            }

            @Override
            public int getRobe() {
                return 21;
            }

            @Override
            public int getMainHand() {
                return 22;
            }

            @Override
            public int getOffHand() {
                return 23;
            }

            @Override
            public int getHoldable() {
                return 24;
            }

            @Override
            public int getAmmo() {
                return 25;
            }

            @Override
            public int getThrown() {
                return 26;
            }

            @Override
            public int getRangedRight() {
                return 27;
            }

            @Override
            public int getQuiver() {
                return 28;
            }

            @Override
            public int getRelic() {
                return 29;
            }
        };
    }

    private ItemSummaryTypeProjection getTypeProjection() {
        return new ItemSummaryTypeProjection() {
            @Override
            public int getConsumable() {
                return 1;
            }

            @Override
            public int getWeapon() {
                return 2;
            }

            @Override
            public int getContainer() {
                return 3;
            }

            @Override
            public int getGem() {
                return 4;
            }

            @Override
            public int getArmor() {
                return 5;
            }

            @Override
            public int getReagent() {
                return 6;
            }

            @Override
            public int getProjectile() {
                return 7;
            }

            @Override
            public int getTradeGoods() {
                return 8;
            }

            @Override
            public int getRecipe() {
                return 9;
            }

            @Override
            public int getQuest() {
                return 9;
            }

            @Override
            public int getKey() {
                return 10;
            }

            @Override
            public int getMiscellaneous() {
                return 11;
            }

            @Override
            public int getGlyph() {
                return 11;
            }

            @Override
            public int getQuiver() {
                return 12;
            }
        };
    }

    private ItemSummaryQualityProjection getQualityProjection() {
        return new ItemSummaryQualityProjection() {
            @Override
            public int getCommon() {
                return 1;
            }

            @Override
            public int getUncommon() {
                return 2;
            }

            @Override
            public int getRare() {
                return 3;
            }

            @Override
            public int getEpic() {
                return 4;
            }

            @Override
            public int getLegendary() {
                return 5;
            }
        };
    }

}
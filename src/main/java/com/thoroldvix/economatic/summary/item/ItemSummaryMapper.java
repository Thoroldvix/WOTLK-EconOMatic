package com.thoroldvix.economatic.summary.item;

import com.thoroldvix.economatic.summary.item.ItemSummaryResponse.Quality;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface ItemSummaryMapper {

    default ItemSummaryResponse toSummaryResponse(ItemSummaryQualityProjection qualityProjection,
                                                  ItemSummaryTypeProjection typeProjection,
                                                  ItemSummarySlotProjection slotProjection,
                                                  long count
    ) {
        ItemSummaryResponse.Quality quality = getQualitySummary(qualityProjection);
        ItemSummaryResponse.Type type = getTypeSummary(typeProjection);
        ItemSummaryResponse.Slot slot = getSummarySlot(slotProjection);
        ItemSummaryResponse.Summary summary = new ItemSummaryResponse.Summary(quality, slot, type, count);

        return new ItemSummaryResponse(summary);
    }

    private ItemSummaryResponse.Slot getSummarySlot(ItemSummarySlotProjection slotProjection) {
        return ItemSummaryResponse.Slot.builder()
                .ammo(slotProjection.getAmmo())
                .hands(slotProjection.getHands())
                .feet(slotProjection.getFeet())
                .head(slotProjection.getHead())
                .legs(slotProjection.getLegs())
                .mainHand(slotProjection.getMainHand())
                .neck(slotProjection.getNeck())
                .chest(slotProjection.getChest())
                .bag(slotProjection.getBag())
                .finger(slotProjection.getFinger())
                .offHand(slotProjection.getOffHand())
                .twoHand(slotProjection.getTwoHand())
                .relic(slotProjection.getRelic())
                .ranged(slotProjection.getRanged())
                .trinket(slotProjection.getTrinket())
                .shield(slotProjection.getShield())
                .quiver(slotProjection.getQuiver())
                .robe(slotProjection.getRobe())
                .shirt(slotProjection.getShirt())
                .holdable(slotProjection.getHoldable())
                .nonEquipable(slotProjection.getNonEquipable())
                .rangedRight(slotProjection.getRangedRight())
                .waist(slotProjection.getWaist())
                .thrown(slotProjection.getThrown())
                .wrists(slotProjection.getWrists())
                .tabard(slotProjection.getTabard())
                .shoulder(slotProjection.getShoulder())
                .back(slotProjection.getBack())
                .weapon(slotProjection.getWeapon())
                .build();
    }

    private Quality getQualitySummary(ItemSummaryQualityProjection qualityProjection) {
        return Quality.builder()
                .common(qualityProjection.getCommon())
                .epic(qualityProjection.getEpic())
                .rare(qualityProjection.getRare())
                .legendary(qualityProjection.getLegendary())
                .uncommon(qualityProjection.getUncommon())
                .build();
    }

    private ItemSummaryResponse.Type getTypeSummary(ItemSummaryTypeProjection typeProjection) {
        return ItemSummaryResponse.Type.builder()
                .miscellaneous(typeProjection.getMiscellaneous())
                .armor(typeProjection.getArmor())
                .key(typeProjection.getKey())
                .glyph(typeProjection.getGlyph())
                .quest(typeProjection.getQuest())
                .gem(typeProjection.getGem())
                .weapon(typeProjection.getWeapon())
                .consumable(typeProjection.getConsumable())
                .container(typeProjection.getContainer())
                .projectile(typeProjection.getProjectile())
                .recipe(typeProjection.getRecipe())
                .reagent(typeProjection.getReagent())
                .tradeGoods(typeProjection.getTradeGoods())
                .quiver(typeProjection.getQuiver())
                .build();
    }
}

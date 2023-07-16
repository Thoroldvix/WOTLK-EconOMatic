package com.thoroldvix.economatic.summary.item;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface ItemSummaryMapper {

    default ItemSummaryResponse toSummaryResponse(ItemSummaryProjection summaryProjection) {

        ItemSummaryResponse.Type type = getSummaryType(summaryProjection);
        ItemSummaryResponse.Quality quality = getSummaryQuality(summaryProjection);
        ItemSummaryResponse.Slot slot = getSummarySlot(summaryProjection);
        ItemSummaryResponse.Summary summary = new ItemSummaryResponse.Summary(quality, slot, type, summaryProjection.getTotal());

        return new ItemSummaryResponse(summary);
    }

    private ItemSummaryResponse.Slot getSummarySlot(ItemSummaryProjection summaryProjection) {
        return ItemSummaryResponse.Slot.builder()
                .ammo(summaryProjection.getAmmo())
                .hands(summaryProjection.getHands())
                .feet(summaryProjection.getFeet())
                .head(summaryProjection.getHead())
                .legs(summaryProjection.getLegs())
                .mainHand(summaryProjection.getMainHand())
                .neck(summaryProjection.getNeck())
                .chest(summaryProjection.getChest())
                .bag(summaryProjection.getBag())
                .finger(summaryProjection.getFinger())
                .offHand(summaryProjection.getOffHand())
                .twoHand(summaryProjection.getTwoHand())
                .relic(summaryProjection.getRelic())
                .ranged(summaryProjection.getRanged())
                .trinket(summaryProjection.getTrinket())
                .shield(summaryProjection.getShield())
                .quiver(summaryProjection.getQuiver())
                .robe(summaryProjection.getRobe())
                .shirt(summaryProjection.getShirt())
                .holdable(summaryProjection.getHoldable())
                .nonEquipable(summaryProjection.getNonEquipable())
                .rangedRight(summaryProjection.getRangedRight())
                .waist(summaryProjection.getWaist())
                .thrown(summaryProjection.getThrown())
                .wrists(summaryProjection.getWrists())
                .tabard(summaryProjection.getTabard())
                .shoulder(summaryProjection.getShoulder())
                .back(summaryProjection.getBack())
                .weapon(summaryProjection.getWeaponSlot())
                .build();
    }

    private ItemSummaryResponse.Quality getSummaryQuality(ItemSummaryProjection summaryProjection) {
        return ItemSummaryResponse.Quality.builder()
                .common(summaryProjection.getCommon())
                .epic(summaryProjection.getEpic())
                .rare(summaryProjection.getRare())
                .legendary(summaryProjection.getLegendary())
                .uncommon(summaryProjection.getUncommon())
                .build();
    }

    private ItemSummaryResponse.Type getSummaryType(ItemSummaryProjection summaryProjection) {
        return ItemSummaryResponse.Type.builder()
                .miscellaneous(summaryProjection.getMiscellaneous())
                .armor(summaryProjection.getArmor())
                .key(summaryProjection.getKey())
                .glyph(summaryProjection.getGlyph())
                .quest(summaryProjection.getQuest())
                .gem(summaryProjection.getGem())
                .weapon(summaryProjection.getWeaponType())
                .consumable(summaryProjection.getConsumable())
                .container(summaryProjection.getContainer())
                .projectile(summaryProjection.getProjectile())
                .recipe(summaryProjection.getRecipe())
                .reagent(summaryProjection.getReagent())
                .tradeGoods(summaryProjection.getTradeGoods())
                .quiver(summaryProjection.getQuiver())
                .build();
    }
}

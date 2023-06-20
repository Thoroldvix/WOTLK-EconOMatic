package com.thoroldvix.pricepal.item;


import com.thoroldvix.pricepal.shared.StringEnumConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemMapper {

    @Mapping(target = "id", source = "id")
    ItemResponse toResponse(Item item);

    @Mapping(target = "type", source = "type", qualifiedByName = "getType")
    @Mapping(target = "quality", source = "quality", qualifiedByName = "getQuality")
    @Mapping(target = "slot", source = "slot", qualifiedByName = "getSlot")
    Item fromRequest(ItemRequest itemRequest);

    List<ItemResponse> toResponseList(List<Item> items);


    default ItemPagedResponse toPagedResponse(Page<Item> items) {

        return ItemPagedResponse.builder()
                .items(toResponseList(items.getContent()))
                .totalPages(items.getTotalPages())
                .totalElements(items.getTotalElements())
                .page(items.getNumber())
                .pageSize(items.getSize())
                .build();
    }

    default ItemSummaryResponse toSummaryResponse(ItemSummaryProjection summaryProjection) {
        ItemSummaryResponse.Type type = getSummaryType(summaryProjection);
        ItemSummaryResponse.Quality quality = getSummaryQuality(summaryProjection);
        ItemSummaryResponse.Slot slot = getSummarySlot(summaryProjection);
        ItemSummaryResponse.Summary  summary = getSummary(type, quality, slot, summaryProjection.getTotal());

        return new ItemSummaryResponse(summary);
    }

   private ItemSummaryResponse.Summary getSummary(ItemSummaryResponse.Type type,
                                           ItemSummaryResponse.Quality quality,
                                           ItemSummaryResponse.Slot slot,
                                           int total) {
        return ItemSummaryResponse.Summary.builder()
               .type(type)
               .quality(quality)
               .slot(slot)
               .total(total)
               .build();
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

    @Named("getType")
    default ItemType getType(String type) {
        return StringEnumConverter.fromString(type, ItemType.class);
    }

    @Named("getQuality")
    default ItemQuality getQuality(String quality) {
        return StringEnumConverter.fromString(quality, ItemQuality.class);
    }

    @Named("getSlot")
    default ItemSlot getSlot(String slot) {
        return StringEnumConverter.fromString(slot, ItemSlot.class);
    }
}


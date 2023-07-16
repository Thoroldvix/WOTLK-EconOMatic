package com.thoroldvix.economatic.summary.item;

import com.thoroldvix.economatic.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
interface ItemSummaryRepository extends JpaRepository<Item, Integer> {

    @Query(value = """
            SELECT
                SUM(CASE WHEN quality = 0 THEN 1 ELSE 0 END) AS common,
                SUM(CASE WHEN quality = 1 THEN 1 ELSE 0 END) AS uncommon,
                SUM(CASE WHEN quality = 2 THEN 1 ELSE 0 END) AS rare,
                SUM(CASE WHEN quality = 3 THEN 1 ELSE 0 END) AS epic,
                SUM(CASE WHEN quality = 4 THEN 1 ELSE 0 END) AS legendary;
                """, nativeQuery = true)
    ItemSummaryQualityProjection getQualitySummary();

    @Query(value = """
            SELECT
            SUM(CASE WHEN type = 0 THEN 1 ELSE 0 END) AS consumable,
                           SUM(CASE WHEN type = 1 THEN 1 ELSE 0 END) AS container,
                           SUM(CASE WHEN type = 2 THEN 1 ELSE 0 END) AS weaponType,
                           SUM(CASE WHEN type = 3 THEN 1 ELSE 0 END) AS gem,
                           SUM(CASE WHEN type = 4 THEN 1 ELSE 0 END) AS armor,
                           SUM(CASE WHEN type = 5 THEN 1 ELSE 0 END) AS reagent,
                           SUM(CASE WHEN type = 6 THEN 1 ELSE 0 END) AS projectile,
                           SUM(CASE WHEN type = 7 THEN 1 ELSE 0 END) AS tradeGoods,
                           SUM(CASE WHEN type = 8 THEN 1 ELSE 0 END) AS recipe,
                           SUM(CASE WHEN type = 9 THEN 1 ELSE 0 END) AS quiver,
                           SUM(CASE WHEN type = 10 THEN 1 ELSE 0 END) AS quest,
                           SUM(CASE WHEN type = 11 THEN 1 ELSE 0 END) AS key,
                           SUM(CASE WHEN type = 12 THEN 1 ELSE 0 END) AS miscellaneous,
                           SUM(CASE WHEN type = 13 THEN 1 ELSE 0 END) AS glyph
                       """, nativeQuery = true)
    ItemSummaryTypeProjection getTypeSummary();

    @Query(value = """
            SELECT
                SUM(CASE WHEN slot = 0 THEN 1 ELSE 0 END) AS nonEquipable,
                SUM(CASE WHEN slot = 1 THEN 1 ELSE 0 END) AS head,
                SUM(CASE WHEN slot = 2 THEN 1 ELSE 0 END) AS neck,
                SUM(CASE WHEN slot = 3 THEN 1 ELSE 0 END) AS shoulder,
                SUM(CASE WHEN slot = 4 THEN 1 ELSE 0 END) AS shirt,
                SUM(CASE WHEN slot = 5 THEN 1 ELSE 0 END) AS chest,
                SUM(CASE WHEN slot = 6 THEN 1 ELSE 0 END) AS waist,
                SUM(CASE WHEN slot = 7 THEN 1 ELSE 0 END) AS legs,
                SUM(CASE WHEN slot = 8 THEN 1 ELSE 0 END) AS feet,
                SUM(CASE WHEN slot = 9 THEN 1 ELSE 0 END) AS wrists,
                SUM(CASE WHEN slot = 10 THEN 1 ELSE 0 END) AS hands,
                SUM(CASE WHEN slot = 11 THEN 1 ELSE 0 END) AS finger,
                SUM(CASE WHEN slot = 12 THEN 1 ELSE 0 END) AS trinket,
                SUM(CASE WHEN slot = 13 THEN 1 ELSE 0 END) AS weaponSlot,
                SUM(CASE WHEN slot = 14 THEN 1 ELSE 0 END) AS shield,
                SUM(CASE WHEN slot = 15 THEN 1 ELSE 0 END) AS ranged,
                SUM(CASE WHEN slot = 16 THEN 1 ELSE 0 END) AS back,
                SUM(CASE WHEN slot = 17 THEN 1 ELSE 0 END) AS twoHand,
                SUM(CASE WHEN slot = 18 THEN 1 ELSE 0 END) AS bag,
                SUM(CASE WHEN slot = 19 THEN 1 ELSE 0 END) AS tabard,
                SUM(CASE WHEN slot = 20 THEN 1 ELSE 0 END) AS robe,
                SUM(CASE WHEN slot = 21 THEN 1 ELSE 0 END) AS mainHand,
                SUM(CASE WHEN slot = 22 THEN 1 ELSE 0 END) AS offHand,
                SUM(CASE WHEN slot = 23 THEN 1 ELSE 0 END) AS holdable,
                SUM(CASE WHEN slot = 24 THEN 1 ELSE 0 END) AS ammo,
                SUM(CASE WHEN slot = 25 THEN 1 ELSE 0 END) AS thrown,
                SUM(CASE WHEN slot = 26 THEN 1 ELSE 0 END) AS rangedRight,
                SUM(CASE WHEN slot = 27 THEN 1 ELSE 0 END) AS quiver,
                SUM(CASE WHEN slot = 28 THEN 1 ELSE 0 END) AS relic
            """, nativeQuery = true)
    ItemSummarySlotProjection getSlotSummary();

}

--items
INSERT INTO public.item (id, quality, type, name, slot, vendor_price)
VALUES (22449, 2, 7, 'Large Prismatic Shard', 0, 0),
       (22446, 1, 7, 'Greater Planar Essence', 0, 0),
       (22445, 0, 7, 'Arcane Dust', 0, 0),
       (22450, 3, 7, 'Void Crystal', 0, 0);
--item prices
INSERT INTO public.item_price (id, min_buyout, historical_value, market_value, quantity, num_auctions, item_id,
                               server_id)
VALUES (265981, 74989, 74743, 78441, 193, 86, 22449, 41003),
       (266124, 249994, 125265, 169363, 419, 98, 22446, 41003),
       (266159, 197981, 113548, 154527, 568, 268, 22450, 41003),
       (266311, 9598, 21735, 24762, 2848, 295, 22445, 41003);


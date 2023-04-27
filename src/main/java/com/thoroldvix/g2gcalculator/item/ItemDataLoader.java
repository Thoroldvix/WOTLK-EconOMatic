package com.thoroldvix.g2gcalculator.item;

/**
 * Was used to load item data from json file into database.
 * No longer in use, since data is now loaded from liquibase sql script instead.
 **/

//@RequiredArgsConstructor
//@Component
//@Slf4j
//@EnableScheduling
//public class ItemDataLoader {
//    private final static long MAX_ITEM_COUNT = 38294;
//    private final static String ITEM_DATA_PATH = "src/main/resources/data.json";
//
//    private final ItemService itemServiceImpl;
//
//    //    @Scheduled(fixedDelay = Long.MAX_VALUE)
//    public void loadItemData() throws IOException {
//        if (itemDataAlreadyLoaded()) {
//            return;
//        }
//        loadAndSaveItemData();
//    }
//
//    private void loadAndSaveItemData() throws IOException {
//        log.info("Loading item data");
//        Instant start = Instant.now();
//        String json = Files.readString(Path.of(ITEM_DATA_PATH));
//        JsonFactory jFactory = new JsonFactory();
//
//        try (JsonParser jParser = jFactory.createParser(json)) {
//            int parsedId = 0;
//            String parsedName = null;
//            String parsedIcon = null;
//            ItemType parsedType = null;
//            ItemQuality parsedQuality = null;
//
//            while (jParser.nextToken() != null) {
//                String fieldName = jParser.getCurrentName();
//
//
//                if ("itemId".equals(fieldName)) {
//                    jParser.nextToken();
//                    parsedId = jParser.getIntValue();
//                }
//                if ("name".equals(fieldName)) {
//                    jParser.nextToken();
//                    parsedName = jParser.getText();
//                }
//                if ("icon".equals(fieldName)) {
//                    jParser.nextToken();
//                    parsedIcon = jParser.getText();
//                }
//                if ("class".equals(fieldName)) {
//                    jParser.nextToken();
//                    parsedType = getItemType(jParser.getText());
//                }
//                if ("quality".equals(fieldName)) {
//                    jParser.nextToken();
//                    parsedQuality = getItemQuality(jParser.getText());
//                }
//                if (itemFullyParsed(parsedId, parsedName, parsedIcon, parsedType, parsedQuality)) {
//                    itemServiceImpl.saveItem(new Item(parsedId, parsedName, parsedIcon, parsedType, parsedQuality));
//                    parsedId = 0;
//                    parsedName = null;
//                    parsedIcon = null;
//                    parsedType = null;
//                    parsedQuality = null;
//                }
//            }
//        }
//        Instant finish = Instant.now();
//        Duration loadTime = Duration.between(start, finish);
//        log.info("Item data loaded in {} ms", loadTime.toMillis());
//    }
//
//    private static boolean itemFullyParsed(int parsedId, String parsedName, String parsedIcon, ItemType parsedType, ItemQuality parsedQuality) {
//        return parsedId != 0 && parsedName != null && parsedIcon != null && parsedQuality != null && parsedType != null;
//    }
//
//
//    private boolean itemDataAlreadyLoaded() {
//        if (itemServiceImpl.getItemCount() >= MAX_ITEM_COUNT) {
//            log.info("Item data already loaded");
//            return true;
//        }
//        return false;
//    }
//
//    private ItemQuality getItemQuality(String quality) {
//        StringEnumConverter<ItemQuality> rarityConverter = new StringEnumConverter<>(ItemQuality.class);
//        return rarityConverter.fromString(quality);
//    }
//
//    private ItemType getItemType(String type) {
//        StringEnumConverter<ItemType> typeConverter = new StringEnumConverter<>(ItemType.class);
//        return typeConverter.fromString(type);
//
//    }
//}


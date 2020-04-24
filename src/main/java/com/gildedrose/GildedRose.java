package com.gildedrose;

class GildedRose {
    public static final String BACKSTAGE = "Backstage passes to a TAFKAL80ETC concert";
    public static final String BRIE = "Aged Brie";
    public static final String SULFURAS = "Sulfuras, Hand of Ragnaros";
    public static final String CONJURED = "Conjured";
    public static final int MAX_QUALITY = 50;
    public static final int LEGENDARY_QUALITY = 80;
    public static final int MIN_QUALITY = 0;
    public static final int DEGRADE = -1;
    public static final int SPEED_NORMAL = 1;
    public static final int SPEED_FASTER = 2;
    public static final int SPEED_CONCERT_FAST = 3;

    Item[] items;
    int day;

    public GildedRose(Item[] items) {
        this.items = items;
        day = 0;
    }

    public void updateQuality() {
        day++;
        for (Item item : items) {
            updateQuality(item);
        }
    }

    private void updateQuality(Item item) {
        advanceTime(item);
        if (isExpired(item)) {
            if (isDegradable(item)) {
                if(isConjured(item)) {
                    degrade(item, SPEED_FASTER * 2);
                } else {
                    degrade(item, SPEED_FASTER);
                }
            }
            if (isUpgradable(item)) {
                upgrade(item, SPEED_FASTER);

            }
        } else {
            if (isDegradable(item)) {
                if(isConjured(item)) {
                    degrade(item, SPEED_FASTER);
                } else {
                    degrade(item);
                }
            }
            if (isUpgradable(item)) {
                if (isBackstagePass(item) && isConcertVerySoon(item)) {
                    upgrade(item, SPEED_CONCERT_FAST);
                } else if (isBackstagePass(item) && isConcertSoon(item)) {
                    upgrade(item, SPEED_FASTER);
                } else {
                    upgrade(item);
                }
            }
        }
        ShowPassed(item);
        ensureLegendaryQuality(item);
    }

    private boolean isConjured(Item item) {
        return item.name.contains(CONJURED);
    }

    private boolean isConcertVerySoon(Item item) {
        return item.sellIn < 5;
    }

    private void ensureLegendaryQuality(Item item) {
        if(isLegendary(item)) {
            item.quality = LEGENDARY_QUALITY;
        }
    }

    private void ShowPassed(Item item) {
        if(isExpired(item) && isBackstagePass(item)) {
            item.quality = MIN_QUALITY;
        }
    }

    private boolean isExpired(Item item) {
        return item.sellIn < 0;
    }

    private void advanceTime(Item item) {
        if (!isLegendary(item)) {
            item.sellIn = item.sellIn - 1;
        }
    }

    private void degrade(Item item) {
        degrade(item, SPEED_NORMAL);
    }
    private void degrade(Item item, int speed) {
        adjustQuality(item, -1 * speed);
    }

    private boolean isDegradable(Item item) {
        return !isLegendary(item) && !isBrie(item) && !isBackstagePass(item);
    }

    private void upgrade(Item item) {
        upgrade(item, SPEED_NORMAL);
    }
    private void upgrade(Item item, int speed) {
        adjustQuality(item, speed);
    }

    private void adjustQuality(Item item, int speed) {
        item.quality = Integer.max(Integer.min(item.quality + speed, MAX_QUALITY), MIN_QUALITY);
        ensureLegendaryQuality(item);
    }

    private boolean isUpgradable(Item item) {
        return !isLegendary(item) && (isBackstagePass(item) || isBrie(item));
    }

    public void qualityOnDay(int day) {
        for (int i = 0; i < day; i++) {
            updateQuality();
        }
    }

    public boolean isBrie(Item item) {
        return isType(item, BRIE);
    }

    public boolean isLegendary(Item item) {
        return isType(item, SULFURAS);
    }

    public boolean isBackstagePass(Item item) {
        return isType(item, BACKSTAGE);
    }

    private boolean isType(Item item, String type) {
        return item.name.equals(type);
    }

    private boolean isConcertSoon(Item item) {
        return item.sellIn < 10;
    }
}
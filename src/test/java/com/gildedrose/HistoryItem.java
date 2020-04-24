package com.gildedrose;

public class HistoryItem extends Item {
  public int originalQuality;
  public int originalSellIn;

  public HistoryItem(String name, int sellIn, int quality) {
    super(name, sellIn, quality);
    originalQuality = quality;
    originalSellIn = sellIn;
  }

}

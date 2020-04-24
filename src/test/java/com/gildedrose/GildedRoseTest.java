package com.gildedrose;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

class GildedRoseTest {

    GildedRose app;
    HistoryItem[] standard;
    HistoryItem[] legendary;
    HistoryItem[] backstage;
    HistoryItem[] brie;
    HistoryItem[] conjured;

    Item[] fullSet = TexttestFixture.items;

    @BeforeEach
    void setUp() {
        standard = new HistoryItem[] {
                new HistoryItem("+5 Dexterity Vest", 10, 20),
                new HistoryItem("Elixir of the Mongoose", 5, 7)
        };
        legendary = new HistoryItem[] {
                new HistoryItem("Sulfuras, Hand of Ragnaros", 0, 80),
                new HistoryItem("Sulfuras, Hand of Ragnaros", -1, 80)
        };
        backstage = new HistoryItem[] {
                new HistoryItem("Backstage passes to a TAFKAL80ETC concert", 15, 20),
                new HistoryItem("Backstage passes to a TAFKAL80ETC concert", 10, 49),
                new HistoryItem("Backstage passes to a TAFKAL80ETC concert", 5, 49)
        };
        brie = new HistoryItem[] {
                new HistoryItem("Aged Brie", 2, 0)
        };

        conjured = new HistoryItem[] {
                new HistoryItem("Conjured Mana Cake", 3, 6)
        };
     }

    @Test
    void testStandardDay0() {
        app = new GildedRose(standard);
        app.qualityOnDay(0);
        assertEquals(20, app.items[0].quality, app.items[0].toString());
        assertEquals(7, app.items[1].quality, app.items[1].toString());
    }
    @Test
    void testStandardDay3() {
        app = new GildedRose(standard);
        app.qualityOnDay(3);
        assertEquals(17, app.items[0].quality, app.items[0].toString());
        assertEquals(4, app.items[1].quality, app.items[1].toString());
    }
    @Test
    void testStandardDay9() {
        app = new GildedRose(standard);
        app.qualityOnDay(9);
        assertEquals(11, app.items[0].quality, app.items[0].toString());
        assertEquals(0, app.items[1].quality, app.items[1].toString());
    }
    @Test
    void testStandardDay50() {
        app = new GildedRose(standard);
        app.qualityOnDay(50);
        assertEquals(0, app.items[0].quality, app.items[0].toString());
        assertEquals(0, app.items[1].quality, app.items[1].toString());
    }

    @RepeatedTest(30)
    void testStandard(RepetitionInfo ri) {
        app = new GildedRose(standard);
        updateQuality(ri.getCurrentRepetition() - 1);
        assertEquals(standardQuality(standard[0],ri.getCurrentRepetition() - 1), app.items[0].quality);
        assertEquals(standardQuality(standard[1],ri.getCurrentRepetition() - 1), app.items[1].quality);
    }

    @RepeatedTest(30)
    void testLegendary(RepetitionInfo ri) {
        app = new GildedRose(legendary);
        updateQuality(ri.getCurrentRepetition() - 1);
        assertEquals(80, app.items[0].quality);
        assertEquals(80, app.items[1].quality);
    }

    @RepeatedTest(30)
    void testBackstage(RepetitionInfo ri) {
        app = new GildedRose(backstage);
        updateQuality(ri.getCurrentRepetition() -1);
        assertEquals(backstageQuality(backstage[0],ri.getCurrentRepetition() - 1), app.items[0].quality, backstage[0].toString());
        assertEquals(backstageQuality(backstage[1],ri.getCurrentRepetition() - 1), app.items[1].quality, backstage[1].toString());
        assertEquals(backstageQuality(backstage[2],ri.getCurrentRepetition() - 1), app.items[2].quality, backstage[2].toString());
    }

    @RepeatedTest(60)
    void testBrie(RepetitionInfo ri) {
        app = new GildedRose(brie);
        updateQuality(ri.getCurrentRepetition() - 1);
        assertEquals(brieQuality(brie[0], ri.getCurrentRepetition() - 1), app.items[0].quality);
    }

    @RepeatedTest(30)
    void testConjured(RepetitionInfo ri) {
        app = new GildedRose(conjured);
        updateQuality(ri.getCurrentRepetition() - 1);
        assertEquals(conjuredQuality(conjured[0], ri.getCurrentRepetition() - 1), app.items[0].quality);
    }

    @Test
    void testFullSet() throws IOException {
        assertEquals(getLocalFile("testOutput.txt"), TexttestFixture.generateUpdates(fullSet, 30));
    }

    private int standardQuality(HistoryItem item, int day) {
        int extraDegrade = Integer.max(day - item.originalSellIn, 0);
        return Integer.max(item.originalQuality - day - extraDegrade, 0);
    }

    private int backstageQuality(HistoryItem item, int day) {
        int daysToShow = Integer.max(item.originalSellIn - day, -1);
        if (daysToShow < 0) {
            return 0;
        } else {
            int quality = item.originalQuality + day;
            if (day > 0) {
                if (daysToShow <= 10) {
                    quality = quality + (10 - daysToShow);
                }
                if (daysToShow <= 5) {
                    quality = quality + (5 - daysToShow);
                }
            }
            return Integer.min(quality, 50);
        }
    }

    private int brieQuality(HistoryItem item, int day) {
        int pastDue = Integer.max(day - item.originalSellIn, 0);
        return Integer.min(item.originalQuality + day + pastDue, 50);
    }

    private int conjuredQuality(HistoryItem item, int day) {
        int extraDegrade = Integer.max(day - item.originalSellIn, 0) * 2;
        return Integer.max(item.originalQuality - day * 2 - extraDegrade, 0);
    }

    private void updateQuality(int num) {
        for (int i = 0; i < num; i++) {
            app.updateQuality();
        }
    }

    private String getLocalFile(String file) throws IOException {
        URL url = this.getClass().getClassLoader().getResource(file);
        BufferedReader reader = new BufferedReader(new FileReader(url.getFile()));
        String nextLine = reader.readLine();
        StringBuilder retval = new StringBuilder();
        while (nextLine != null) {
            retval.append(nextLine).append(System.lineSeparator());
            nextLine = reader.readLine();
        }
        return retval.append(System.lineSeparator()).toString();
    }
}
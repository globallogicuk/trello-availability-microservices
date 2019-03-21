package com.function;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.zip.DataFormatException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;


public class TestCard {
    private ClassLoader classLoader = getClass().getClassLoader();

    @Test
    public void testCardHalfHourSlotMissing() throws IOException, DataFormatException {
        String card = IOUtils.toString(classLoader.getResourceAsStream("tuesdayHalfHourBooked.txt"), "UTF-8");
        Card processedCard = new Card(card);
        assertEquals(card, processedCard.toString());
    }
}
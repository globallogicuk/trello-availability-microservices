package com.function;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.zip.DataFormatException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;


public class TestDay {
    private ClassLoader classLoader = getClass().getClassLoader();

    @Test
    public void testOneDay() throws IOException, DataFormatException {
        String card = IOUtils.toString(classLoader.getResourceAsStream("tuesdayAvailable.txt"), "UTF-8");
        String day = card.split("Tuesday")[0];
        Day processedDay = new Day(day);
        assertEquals(true, processedDay.getTimeSlot("09:00").isAvailable());
    }

    @Test
    public void testDayHalfHourSlotMissing() throws IOException, DataFormatException {
        String card = IOUtils.toString(classLoader.getResourceAsStream("tuesdayHalfHourBooked.txt"), "UTF-8");
        String day = card.split("Tuesday")[1].split("Wednesday")[0];
        Day processedDay = new Day(day);
        assertEquals(true, processedDay.getTimeSlot("09:00").isAvailable());
        assertEquals(false, processedDay.getTimeSlot("10:30").isAvailable());
        assertEquals(day, "\n"+processedDay.toString());
    }

    @Test
    public void testDayOneHourSlotMissing() throws IOException, DataFormatException {
        String card = IOUtils.toString(classLoader.getResourceAsStream("tuesdayOneHourBooked.txt"), "UTF-8");
        String day = card.split("Tuesday")[1].split("Wednesday")[0];
        Day processedDay = new Day(day);
        assertEquals(true, processedDay.getTimeSlot("09:00").isAvailable());
        assertEquals(false, processedDay.getTimeSlot("10:30").isAvailable());
        assertEquals(false, processedDay.getTimeSlot("11:00").isAvailable());
        assertEquals(day, "\n"+processedDay.toString());

    }

    @Test
    public void testDayInterviewBooked() throws IOException, DataFormatException {
        String card = IOUtils.toString(classLoader.getResourceAsStream("tuesdayOneHourInterviewBooked.txt"), "UTF-8");
        String day = card.split("Tuesday")[1].split("Wednesday")[0];
        Day processedDay = new Day(day);
        assertEquals(true, processedDay.getTimeSlot("09:00").isAvailable());
        assertEquals(true, processedDay.getTimeSlot("10:30").isAvailable());
        assertEquals(true, processedDay.getTimeSlot("11:00").isAvailable());
        assertEquals("Interview booked", processedDay.getTimeSlot("10:30").getComments());
        assertEquals("Interview booked", processedDay.getTimeSlot("11:00").getComments());
        assertEquals(day, "\n"+processedDay.toString());
    }
}
package com.function;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;

public class Day {

    private final static Logger LOGGER = Logger.getLogger(Day.class.getName());

    private LinkedHashMap<String, TimeSlot> timeSlots = new LinkedHashMap<>();

    private String dayComments = "";

    private void initialiseTimeSlots() {
        timeSlots.put("08:30", new TimeSlot("08:30","09:00"));
        timeSlots.put("09:00", new TimeSlot("09:00","09:30"));
        timeSlots.put("09:30", new TimeSlot("09:30","10:00"));
        timeSlots.put("10:00", new TimeSlot("10:00","10:30"));
        timeSlots.put("10:30", new TimeSlot("10:30","11:00"));
        timeSlots.put("11:00", new TimeSlot("11:00","11:30"));
        timeSlots.put("11:30", new TimeSlot("11:30","12:00"));
        timeSlots.put("12:00", new TimeSlot("12:00","12:30"));
        timeSlots.put("12:30", new TimeSlot("12:30","13:00"));
        timeSlots.put("13:00", new TimeSlot("13:00","13:30"));
        timeSlots.put("13:30", new TimeSlot("13:30","14:00"));
        timeSlots.put("14:00", new TimeSlot("14:00","14:30"));
        timeSlots.put("14:30", new TimeSlot("14:30","15:00"));
        timeSlots.put("15:00", new TimeSlot("15:00","15:30"));
        timeSlots.put("15:30", new TimeSlot("15:30","16:00"));
        timeSlots.put("16:00", new TimeSlot("16:00","16:30"));
        timeSlots.put("16:30", new TimeSlot("16:30","17:00"));
        timeSlots.put("17:00", new TimeSlot("17:00","17:30"));
    }

    public Day() {
        initialiseTimeSlots();
    }

    /**
     * Initialise a Day object by parsing a trello card String
     */
    public Day(String trelloCard) throws IOException, DataFormatException {
        //Get any comments for the day
        String result = trelloCard.split("\\R", 2)[0];
        String extractedComments = result.substring(result.indexOf(" ")+1);
        if (extractedComments != null && !extractedComments.isEmpty()) {
            dayComments = " " + result.substring(result.indexOf(" ")+1);
        }
        initialiseTimeSlots();
        //Set all days to unavailable so any gaps are retained
        for (Map.Entry<String, TimeSlot> entry : timeSlots.entrySet()) {
            entry.getValue().setSlotAvailability(false);
        }

        //Read in the card string
        BufferedReader br = new BufferedReader(new StringReader(trelloCard));
        String line=null;
        while ( ( line=br.readLine()) != null) {
            String startTime = line.split(" - ")[0];
            if (timeSlots.containsKey(startTime)) {
                timeSlots.put(startTime, new TimeSlot(line));
            } else {
                LOGGER.info("Unable to process line: "+line);
            }
        }
    }

    public TimeSlot getTimeSlot(String startTime) {
        return timeSlots.get(startTime);
    }

    public String getDayComments() {
        return dayComments;
    }

    @Override
    public String toString() {
        //Print a new line at start of day
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        timeSlots.forEach((k,v) -> sb.append(v.toString()+"\n"));
        //New line at end of day
        sb.append("\n");
        return sb.toString();
    }

	public void handleEvent(LocalTime startTime, LocalTime endTime, String description) {
        //Iterate through all the timeslots for the day and see if any part of the event falls in that slot
        for (Map.Entry<String, TimeSlot> entry : timeSlots.entrySet()) {
            TimeSlot s = entry.getValue();
            if(isEventInSlot(startTime, endTime, s)) {
                if (org.apache.commons.lang3.StringUtils.containsIgnoreCase(description, "interview")) {
                    s.setComments("Interview booked");
                } else {
                    s.setSlotAvailability(false);
                }
                timeSlots.put(entry.getKey(), s);
            }
        }
    }
    
    private boolean isEventInSlot(LocalTime start, LocalTime end, TimeSlot slot) {
        if (start.isBefore(slot.getLocalEndTime()) && end.isAfter(slot.getLocalStartTime())) {
            return true;
        } else {
            return false;
        }
    }

}
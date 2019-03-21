package com.function;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

public class TimeSlot {
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean slotAvailable;
    private String slotComments;

    private String convertTime(LocalTime inputTime){
        return inputTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private LocalTime convertTime(String inputTime) {
        return LocalTime.parse(inputTime, DateTimeFormatter.ofPattern("HH:mm"));
    }

    public TimeSlot(LocalTime startTime, LocalTime endTime, String slotComments) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.slotComments = slotComments;
        this.slotAvailable = true;
    }

    public TimeSlot(String startTime, String endTime) {
        this.startTime = convertTime(startTime);
        this.endTime = convertTime(endTime);
        this.slotAvailable = true;
    }

    public TimeSlot(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.slotAvailable = true;
    }

    public String getStartTime() {
        return convertTime(this.startTime);
    }

    public String getEndTime(){
        return convertTime(this.endTime);
    }

    public LocalTime getLocalStartTime() {
        return this.startTime;
    }

    public LocalTime getLocalEndTime(){
        return this.endTime;
    }

    public boolean isAvailable() {
        return slotAvailable;
    }

    public void setComments(String comments) {
        this.slotComments = comments;
    }

    public String getComments() {
        return slotComments;
    }

    public TimeSlot(String trelloTimeSlot) throws DataFormatException{
        //Get the start and end times
        Pattern startEndPattern = Pattern.compile("([0-9:]+) - ([0-9:]+)");
        Pattern fullPattern = Pattern.compile("[0-9:]+ - [0-9:]+ - (.*)");
        Matcher m = startEndPattern.matcher(trelloTimeSlot);
        if (m.find()) {
            this.startTime = convertTime(m.group(1));
            this.endTime = convertTime(m.group(2));
            this.slotAvailable = true;
            //Try and get any comments
            Matcher fullMatcher = fullPattern.matcher(trelloTimeSlot);
            if (fullMatcher.find()) {
                this.slotComments = fullMatcher.group(1);
            }
        } else {
            throw new DataFormatException("Unable to parse timeslot: "+trelloTimeSlot);
        }
    }

    public void setSlotAvailability(boolean available) {
        this.slotAvailable = available;
    }

    @Override
    public String toString() {
        if(this.slotAvailable)
        {
            if(this.slotComments == null) {
                return this.startTime + " - " + this.endTime;
            } else {
                return this.startTime + " - " + this.endTime + " - " + this.slotComments;
            }
        } else {
            return "";
         }
    }
}
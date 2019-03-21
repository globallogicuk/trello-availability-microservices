package com.function;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

import org.apache.commons.text.*;

public class Card {

    private LinkedHashMap<DayOfWeek, Day> days = new LinkedHashMap<>();

    public Card(String cardFromTrello) throws IOException, DataFormatException {
        days.put(DayOfWeek.MONDAY, processSection("Monday", "^Tuesday", cardFromTrello));
        days.put(DayOfWeek.TUESDAY, processSection("Tuesday", "^Wednesday", cardFromTrello));
        days.put(DayOfWeek.WEDNESDAY, processSection("Wednesday", "^Thursday", cardFromTrello));
        days.put(DayOfWeek.THURSDAY, processSection("Thursday", "^Friday", cardFromTrello));
        days.put(DayOfWeek.FRIDAY, processSection("Friday", "$", cardFromTrello));
    }

    private Day processSection(String fromDay, String toDay, String fullCard) throws IOException, DataFormatException {
        
        Pattern p = Pattern.compile("^"+fromDay+"(.*)(?="+toDay+")", Pattern.DOTALL|Pattern.MULTILINE);
        Matcher m = p.matcher(fullCard);
        if (m.find()) {
            return new Day(m.group(1));
        } else {
            throw new IOException("Unable to find section of card between "+fromDay+" and "+toDay);
        }
    }

    protected void handleEvent(LocalDateTime startDateTime, LocalDateTime endDateTime, String description) {
        DayOfWeek dayOfWeek = startDateTime.getDayOfWeek();
        LocalTime startTime = startDateTime.toLocalTime();
        LocalTime endTime = endDateTime.toLocalTime();
        days.get(dayOfWeek).handleEvent(startTime, endTime, description);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        days.forEach((k,v) ->
        {
            sb.append(WordUtils.capitalizeFully(k.toString())+"\n");
            sb.append(v.toString());
        });
        return sb.toString();
    }
}
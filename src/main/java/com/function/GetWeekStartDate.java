package com.function;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.*;

import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import org.apache.commons.text.*;

/**
 * Azure Functions with HTTP Trigger.
 */
public class GetWeekStartDate {
    /**
     * This function listens at endpoint "/api/GetWeekStartDate". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/GetWeekStartDate
     * 2. curl {your host}/api/GetWeekStartDate?name=HTTP%20Query
     */
    @FunctionName("GetWeekStartDate")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        String query = request.getQueryParameters().get("startTime");
        String startTime = request.getBody().orElse(query);

        if (startTime == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a startTime on the query string or in the request body").build();
        } else {
            context.getLogger().info("Received startTime of "+startTime);

            return request.createResponseBuilder(HttpStatus.OK).body(getWeekCommencing(startTime)).build();
        }
    }

    /**
     * Take the string, convert to a timestamp and find when the start of the week was
     */
    private String getWeekCommencing(String timestamp)
    {
        LocalDateTime parsedDate = LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSXXX"));
        TemporalField fieldISO = WeekFields.of(Locale.UK).dayOfWeek();
        LocalDateTime weekStart = parsedDate.with(fieldISO, 1);
        return ordinal(weekStart.getDayOfMonth()) + " " + WordUtils.capitalizeFully(weekStart.getMonth().toString());
    }

    public static String ordinal(int i) {
        String[] sufixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
        switch (i % 100) {
        case 11:
        case 12:
        case 13:
            return i + "th";
        default:
            return i + sufixes[i % 10];
    
        }
    }
}

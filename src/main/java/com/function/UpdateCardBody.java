package com.function;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;
import java.util.zip.DataFormatException;
import org.json.*;

import com.microsoft.azure.functions.annotation.*;

import com.microsoft.azure.functions.*;

/**
 * Azure Functions with HTTP Trigger.
 */
public class UpdateCardBody {
    /**
     * This function listens at endpoint "/api/UpdateCardBody". Two ways to invoke
     * it using "curl" command in bash: 1. curl -d "HTTP Body" {your
     * host}/api/UpdateCardBody 2. curl {your
     * host}/api/UpdateCardBody?name=HTTP%20Query
     * 
     * @throws DataFormatException
     * @throws IOException
     */
    @FunctionName("UpdateCardBody")
    public HttpResponseMessage run(@HttpTrigger(name = "req", methods = {
            HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<String> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameters
        
        JSONObject jsonBody = new JSONObject(request.getBody());
        String cardBody = jsonBody.getString("card");
        String eventStartTime = request.getQueryParameters().get("eventStartTime");
        String eventEndTime = request.getQueryParameters().get("eventEndTime");
        String eventSubject = request.getQueryParameters().get("eventSubject");

        if (Stream.of(cardBody, eventStartTime, eventEndTime, eventSubject).anyMatch(x -> x == null)) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Mandatory query paramter missing").build();
        } else {
            //Serialise the card
            Card processedCard;
            try {
                processedCard = new Card(cardBody);
            } catch (Exception e) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Exception thrown when processing message: "+e.getMessage()).build();
            }            
            //Find the relevant day
            LocalDateTime parsedStart = LocalDateTime.parse(eventStartTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSXXX"));
            LocalDateTime parsedEnd = LocalDateTime.parse(eventEndTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSXXX"));

            processedCard.handleEvent(parsedStart, parsedEnd, eventSubject);
            return request.createResponseBuilder(HttpStatus.OK).body(processedCard.toString()).build();
        }
    }
}

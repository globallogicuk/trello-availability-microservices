package com.function;

import org.junit.Test;

import com.microsoft.azure.functions.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

/**
 * Unit test for Function class.
 */
public class UpdateCardBodyTest {

    ClassLoader classLoader = getClass().getClassLoader();

    public HttpResponseMessage runQuery(Map<String, String> queryParams, String queryBody) throws IOException, DataFormatException {
        // Setup
        @SuppressWarnings("unchecked")
        final HttpRequestMessage<String> req = mock(HttpRequestMessage.class);
        doReturn(queryParams).when(req).getQueryParameters();
        JSONObject body = new JSONObject().put("card", queryBody);
        doReturn(body.toString()).when(req).getBody();

        doAnswer(new Answer<HttpResponseMessage.Builder>() {
            @Override
            public HttpResponseMessage.Builder answer(InvocationOnMock invocation) {
                HttpStatus status = (HttpStatus) invocation.getArguments()[0];
                return new HttpResponseMessageMock.HttpResponseMessageBuilderMock().status(status);
            }
        }).when(req).createResponseBuilder(any(HttpStatus.class));

        final ExecutionContext context = mock(ExecutionContext.class);
        doReturn(Logger.getGlobal()).when(context).getLogger();

        // Invoke
        final HttpResponseMessage ret = new UpdateCardBody().run(req, context);
        return ret;
    }

    @Test
    public void missingStartDate() throws Exception {
        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("eventEndTime", "");
        queryParams.put("eventSubject", "");


        HttpResponseMessage ret = runQuery(queryParams, "");

        // Verify
        assertEquals(HttpStatus.BAD_REQUEST, ret.getStatus());
    }

    @Test
    public void missingEndDate() throws Exception {
        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("eventStartTime", "");
        queryParams.put("eventSubject", "");


        HttpResponseMessage ret = runQuery(queryParams, "");

        // Verify
        assertEquals(HttpStatus.BAD_REQUEST, ret.getStatus());
    }

    @Test
    public void missingEventSubject() throws Exception {
        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("eventStartTime", "");
        queryParams.put("eventEndTime", "");


        HttpResponseMessage ret = runQuery(queryParams, "");

        // Verify
        assertEquals(HttpStatus.BAD_REQUEST, ret.getStatus());
    }

    /**
     * This a positive test case;
     * the new calendar event overlaps with a time slot that was previously available on trello
     * Remove the slot from the trello card
     */
    @Test
    public void removeAvailableSlotDueToHalfHourEvent() throws Exception {
        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("eventStartTime", "2019-03-19T10:30:00.0000000+00:00");
        queryParams.put("eventEndTime", "2019-03-19T11:00:00.0000000+00:00");
        queryParams.put("eventSubject", "SomeEvent");

        HttpResponseMessage ret = runQuery(queryParams, IOUtils.toString(classLoader.getResourceAsStream("tuesdayAvailable.txt"), "UTF-8"));

        // Verify
        assertEquals(ret.getStatus(), HttpStatus.OK);
        assertEquals(IOUtils.toString(classLoader.getResourceAsStream("tuesdayHalfHourBooked.txt"), "UTF-8"), ret.getBody().toString());
    }

    /**
     * This a positive test case;
     * the new calendar event overlaps with a time slot that was previously available on trello
     * Remove the slot from the trello card
     */
    @Test
    public void removeAvailableSlotDueToOneHourEvent() throws Exception {
        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("eventStartTime", "2019-03-19T10:30:00.0000000+00:00");
        queryParams.put("eventEndTime", "2019-03-19T11:30:00.0000000+00:00");
        queryParams.put("eventSubject", "SomeEvent");

        HttpResponseMessage ret = runQuery(queryParams, IOUtils.toString(classLoader.getResourceAsStream("tuesdayAvailable.txt"), "UTF-8"));

        // Verify
        assertEquals(ret.getStatus(), HttpStatus.OK);
        assertEquals(IOUtils.toString(classLoader.getResourceAsStream("tuesdayOneHourBooked.txt"), "UTF-8"), ret.getBody().toString());
    }

    /**
     * This a positive test case;
     * the new calendar event overlaps with a time slot that was previously available on trello
     * However what's gone in is an interview
     */
    @Test
    public void removeAvailableSlotDueToHalfHourInterview() throws Exception {
        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("eventStartTime", "2019-03-19T10:30:00.0000000+00:00");
        queryParams.put("eventEndTime", "2019-03-19T11:00:00.0000000+00:00");
        queryParams.put("eventSubject", "Phone interview");

        HttpResponseMessage ret = runQuery(queryParams, IOUtils.toString(classLoader.getResourceAsStream("tuesdayAvailable.txt"), "UTF-8"));

        // Verify
        assertEquals(ret.getStatus(), HttpStatus.OK);
        assertEquals(IOUtils.toString(classLoader.getResourceAsStream("tuesdayHalfHourInterviewBooked.txt"), "UTF-8"), ret.getBody().toString());
    }

    /**
     * This a positive test case;
     * the new calendar event overlaps with a time slot that was previously available on trello
     * However what's gone in is an interview
     */
    @Test
    public void removeAvailableSlotDueToOneHourInterview() throws Exception {
        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("eventStartTime", "2019-03-19T10:30:00.0000000+00:00");
        queryParams.put("eventEndTime", "2019-03-19T11:30:00.0000000+00:00");
        queryParams.put("eventSubject", "Phone interview");

        HttpResponseMessage ret = runQuery(queryParams, IOUtils.toString(classLoader.getResourceAsStream("tuesdayAvailable.txt"), "UTF-8"));

        // Verify
        assertEquals(ret.getStatus(), HttpStatus.OK);
        assertEquals(IOUtils.toString(classLoader.getResourceAsStream("tuesdayOneHourInterviewBooked.txt"), "UTF-8"), ret.getBody().toString());
    }

    /**
     * This a positive test case;
     * the new calendar event overlaps with a time slot that was previously available on trello
     * Remove the slot from the trello card
     * The event falls at an unusual start and end times
     */
    @Test
    public void removeAvailableSlotDueTo25MinuteEvent() throws Exception {
        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("eventStartTime", "2019-03-19T10:35:00.0000000+00:00");
        queryParams.put("eventEndTime", "2019-03-19T10:55:00.0000000+00:00");
        queryParams.put("eventSubject", "SomeEvent");

        HttpResponseMessage ret = runQuery(queryParams, IOUtils.toString(classLoader.getResourceAsStream("tuesdayAvailable.txt"), "UTF-8"));

        // Verify
        assertEquals(ret.getStatus(), HttpStatus.OK);
        assertEquals(IOUtils.toString(classLoader.getResourceAsStream("tuesdayHalfHourBooked.txt"), "UTF-8"), ret.getBody().toString());
    }

    /**
     * This a positive test case;
     * the new calendar event overlaps with a time slot that was previously available on trello
     * Remove the slot from the trello card
     * The event falls at an unusual start and end times
     */
    @Test
    public void removeAvailableSlotDueTo35MinuteEvent() throws Exception {
        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("eventStartTime", "2019-03-19T10:35:00.0000000+00:00");
        queryParams.put("eventEndTime", "2019-03-19T11:15:00.0000000+00:00");
        queryParams.put("eventSubject", "SomeEvent");

        HttpResponseMessage ret = runQuery(queryParams, IOUtils.toString(classLoader.getResourceAsStream("tuesdayAvailable.txt"), "UTF-8"));

        // Verify
        assertEquals(ret.getStatus(), HttpStatus.OK);
        assertEquals(IOUtils.toString(classLoader.getResourceAsStream("tuesdayOneHourBooked.txt"), "UTF-8"), ret.getBody().toString());
    }
}

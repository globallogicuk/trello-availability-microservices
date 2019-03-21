package com.function;

import org.junit.Test;

import com.microsoft.azure.functions.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Unit test for Function class.
 */
public class GetWeekStartDateTest {

    public HttpResponseMessage runQuery(Map<String, String> queryParams) {
        // Setup
        @SuppressWarnings("unchecked")
        final HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);
        doReturn(queryParams).when(req).getQueryParameters();

        final Optional<String> queryBody = Optional.empty();
        doReturn(queryBody).when(req).getBody();

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
        final HttpResponseMessage ret = new GetWeekStartDate().run(req, context);
        return ret;
    }

    @Test
    public void tuesdayMidMonth() throws Exception {
        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("startTime", "2019-03-19T10:30:00.0000000+00:00");

        HttpResponseMessage ret = runQuery(queryParams);

        // Verify
        assertEquals(ret.getStatus(), HttpStatus.OK);
        assertEquals("18th March", ret.getBody());
    }

    @Test
    public void mondayMidMonth() throws Exception {
        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("startTime", "2019-03-18T10:30:00.0000000+00:00");

        HttpResponseMessage ret = runQuery(queryParams);

        // Verify
        assertEquals(ret.getStatus(), HttpStatus.OK);
        assertEquals("18th March", ret.getBody());
    }

    @Test
    public void sundayMidMonth() throws Exception {
        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("startTime", "2019-03-24T10:30:00.0000000+00:00");

        HttpResponseMessage ret = runQuery(queryParams);

        // Verify
        assertEquals(ret.getStatus(), HttpStatus.OK);
        assertEquals("18th March", ret.getBody());
    }

    @Test
    public void fridayNewMonth() throws Exception {
        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("startTime", "2019-03-01T10:30:00.0000000+00:00");

        HttpResponseMessage ret = runQuery(queryParams);

        // Verify
        assertEquals(ret.getStatus(), HttpStatus.OK);
        assertEquals("25th February", ret.getBody());
    }
}

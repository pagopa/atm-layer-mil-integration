package it.gov.pagopa.miladapter.resttemplate;

import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.TimeValue;
import org.apache.http.StatusLine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.time.Duration;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class CustomHttpRequestRetryStrategyTest {
    private CustomHttpRequestRetryStrategy customHttpRequestRetryStrategy;
    @BeforeEach
    public void setup() {
        int maxRetry = 2;
        int retryInterval = 500;
        customHttpRequestRetryStrategy = new CustomHttpRequestRetryStrategy(maxRetry, retryInterval);
    }
    @Test
    public void testRetryRequestMaxRetry() {
        HttpRequest httpRequest = Mockito.mock(HttpRequest.class);
        HttpContext httpContext = Mockito.mock(HttpContext.class);
        IOException IoException = Mockito.mock(IOException.class);

        int currentRetryCountMin = 1;

        boolean resultTrue = customHttpRequestRetryStrategy.retryRequest(httpRequest, IoException, currentRetryCountMin, httpContext);
        Assertions.assertTrue(resultTrue);

        int currentRetryCountMax = 3;

        boolean resultFalse = customHttpRequestRetryStrategy.retryRequest(httpRequest, IoException, currentRetryCountMax, httpContext);
        Assertions.assertFalse(resultFalse);
    }
    @Test
    public void testRetryRequestOk() {
        HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
        StatusLine statusLine = Mockito.mock(StatusLine.class);

        when(httpResponse.getCode()).thenReturn(200);
        when(statusLine.getStatusCode()).thenReturn(200);

        boolean result = customHttpRequestRetryStrategy.retryRequest(httpResponse, 2, Mockito.mock(HttpContext.class));
        Assertions.assertFalse(result);
    }
    @Test
    public void testRetryRequestKo() {
        HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
        StatusLine statusLine = Mockito.mock(StatusLine.class);

        when(httpResponse.getCode()).thenReturn(400);
        when(statusLine.getStatusCode()).thenReturn(400);

        boolean result = customHttpRequestRetryStrategy.retryRequest(httpResponse, 2, Mockito.mock(HttpContext.class));
        Assertions.assertTrue(result);
    }
    @Test
    public void testGetRetryInterval() {
        HttpRequest httpRequest = Mockito.mock(HttpRequest.class);
        HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
        HttpContext httpContext = Mockito.mock(HttpContext.class);
        IOException IoException = Mockito.mock(IOException.class);
        int exceptionCounter = 2;
        int retry = 2;

        TimeValue resultRequest = customHttpRequestRetryStrategy.getRetryInterval(httpRequest, IoException, exceptionCounter, httpContext);

        Assertions.assertEquals(TimeValue.of(Duration.ofMillis(500)), resultRequest);

        TimeValue resultResponse = customHttpRequestRetryStrategy.getRetryInterval(httpResponse, retry, httpContext);

        Assertions.assertEquals(TimeValue.of(Duration.ofMillis(500)), resultResponse);
    }
}

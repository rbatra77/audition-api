package com.audition.configuration;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class ResponseHeaderInjector implements Filter {

    // TODO Inject openTelemetry trace and span Ids in the response headers.

    public static final String TRACE_ID_HEADER = "X-B3-TraceId";
    public static final String SPAN_ID_HEADER = "X-B3-SpanId";

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
        throws IOException, ServletException {
        final HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader(TRACE_ID_HEADER, MDC.get("traceId"));
        httpServletResponse.setHeader(SPAN_ID_HEADER, MDC.get("spanId"));
        chain.doFilter(request, response);

    }

}

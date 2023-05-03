package com.audition.integration.interceptors;

import com.audition.common.logging.AuditionLogger;
import java.io.IOException;
import java.nio.charset.Charset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

@Component
public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingInterceptor.class);
    private AuditionLogger logger;

    @Override
    public ClientHttpResponse intercept(final HttpRequest request, final byte[] body,
        final ClientHttpRequestExecution execution)
        throws IOException {

        logRequest(request, body);

        final ClientHttpResponse response = execution.execute(request, body);

        logResponse(response);

        return response;
    }

    private void logRequest(final HttpRequest request, final byte[] body) throws IOException {

        if (LOG.isDebugEnabled()) { //the check is just to avoind PMD issue .. below functions already have the checks
            logger.debug(LOG, "===Request is being sent to downstream===");
            //Assumption is that we need to log everything and there is nothing confidential
            logger.debug(LOG, "URI: " + request.getURI() + ", Method: " + request.getMethod());
            logger.debug(LOG, "Headers: " + request.getHeaders());
            logger.debug(LOG, "Request body: " + new String(body, "UTF-8"));
            logger.debug(LOG, "===end of request===");
        }
    }

    private void logResponse(final ClientHttpResponse response) throws IOException {

        if (LOG.isDebugEnabled()) { //the check is just to avoind PMD issue .. below functions already have the checks
            logger.debug(LOG, "===Response received===");
            //Assumption is that we need to log everything and there is nothing confidential
            logger.debug(LOG,
                "Status code: " + response.getStatusCode() + ", Status text: " + response.getStatusText());
            logger.debug(LOG, "Headers: " + response.getHeaders());
            logger.debug(LOG,
                "Response body: " + StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()));
            logger.debug(LOG, "===end of response===");
        }

    }

    @Autowired
    public void setLogger(final AuditionLogger logger) {
        this.logger = logger;
    }

    public AuditionLogger getLogger() {
        return logger;
    }
}

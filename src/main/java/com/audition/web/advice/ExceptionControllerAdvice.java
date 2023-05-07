package com.audition.web.advice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

import com.audition.common.exception.SystemException;
import com.audition.common.logging.AuditionLogger;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ConstraintViolationException;
import java.util.concurrent.CompletionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {

    public static final String DEFAULT_TITLE = "API Error Occurred";

    public static final String DOWNSTREAM_UNAVAILABLE = "Downstream Service Unavailable";

    public static final String CONSTRAINT_VIOLATION_OCCURRED = "Constraint violation occurred";
    private static final Logger LOG = LoggerFactory.getLogger(ExceptionControllerAdvice.class);
    private static final String DEFAULT_MESSAGE = "API Error occurred. Please contact support or administrator.";

    private AuditionLogger logger;

    @ExceptionHandler(HttpClientErrorException.class)
    ProblemDetail handleHttpClientException(final HttpClientErrorException e) {

        return createProblemDetail(e.getStatusCode(), DEFAULT_TITLE, e);

    }


    @ExceptionHandler(Exception.class)
    ProblemDetail handleMainException(final Exception e) {
        // TODO Add handling for Exception
        return createProblemDetail(INTERNAL_SERVER_ERROR, DEFAULT_TITLE, e);

    }

    @ExceptionHandler(CompletionException.class)
    ProblemDetail handleCompletionException(final CompletionException e) {
        
        if (e.getMessage().contains(SystemException.class.getName())) {
            //That means raised by Circuit Breaker
            return createProblemDetail(SERVICE_UNAVAILABLE, DOWNSTREAM_UNAVAILABLE, e);
        } else {
            return createProblemDetail(INTERNAL_SERVER_ERROR, DEFAULT_TITLE, e);
        }

    }

    @ExceptionHandler(ConstraintViolationException.class)
    ProblemDetail handleConstraintViolationException(final ConstraintViolationException e) {

        return createProblemDetail(BAD_REQUEST, CONSTRAINT_VIOLATION_OCCURRED, e);

    }

    @ExceptionHandler(SystemException.class)
    ProblemDetail handleSystemException(final SystemException e) {
        // TODO `Add Handling for SystemException

        try {
            return createProblemDetail(HttpStatusCode.valueOf(e.getStatusCode()), e.getTitle(), e);
        } catch (final IllegalArgumentException iae) {

            return createProblemDetail(INTERNAL_SERVER_ERROR, e.getTitle(), e);
        }

    }

    private ProblemDetail createProblemDetail(final HttpStatusCode statusCode, final String title, final Exception e) {
        final ProblemDetail problemDetail = ProblemDetail.forStatus(statusCode);
        problemDetail.setDetail(getMessageFromException(e));
        problemDetail.setTitle(title);
        logger.logStandardProblemDetail(LOG, problemDetail, e);
        return problemDetail;
    }

    private String getMessageFromException(final Exception exception) {
        if (StringUtils.isNotBlank(exception.getMessage())) {
            return exception.getMessage();
        }
        return DEFAULT_MESSAGE;
    }

    @Autowired
    public void setLogger(final AuditionLogger logger) {
        this.logger = logger;
    }

    public AuditionLogger getLogger() {
        return logger;
    }

}




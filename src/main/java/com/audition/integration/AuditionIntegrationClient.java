package com.audition.integration;

import com.audition.common.exception.SystemException;
import com.audition.configuration.AppConfig;
import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import com.audition.web.advice.ExceptionControllerAdvice;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class AuditionIntegrationClient {

    private AppConfig config;
    private RestTemplate restTemplate;

    @CircuitBreaker(name = "backend", fallbackMethod = "fallbackPosts")
    public List<AuditionPost> getPosts(final Optional<Integer> userId, final Optional<String> title) {
        // TODO make RestTemplate call to get Posts from https://jsonplaceholder.typicode.com/posts

        final ResponseEntity<List<AuditionPost>> response = restTemplate.exchange(
            constructGetPostsUri(userId, title),
            HttpMethod.GET, null, new ParameterizedTypeReference<List<AuditionPost>>() {
            });
        return response.getBody();

    }

    private URI constructGetPostsUri(final Optional<Integer> userId, final Optional<String> title) {

        return UriComponentsBuilder.fromUri(URI.create(config.getAuditionBaseUri() + config.getAuditionPostsSuffix()))
            .queryParamIfPresent(config.getAuditionUserIdFilter(), userId)
            .queryParamIfPresent(config.getAuditionTitleFilter(), title)
            .build().toUri();

    }

    @CircuitBreaker(name = "backend", fallbackMethod = "fallbackPost")
    public AuditionPost getPostById(final Integer id) {
        // TODO get post by post ID call from https://jsonplaceholder.typicode.com/posts/
        try {
            return restTemplate.getForObject(config.getAuditionBaseUri() + config.getAuditionPostsSuffix() + id,
                AuditionPost.class);
        } catch (final HttpClientErrorException e) {

            throw new SystemException("Error while accessing a Post with id " + id, e.getStatusText(),
                e.getStatusCode().value(), e);

            // TODO Find a better way to handle the exception so that the original error message is not lost. Feel free to change this function.
            // One generic function handling all status codes would be cleaner

        }
    }

    // TODO Write a method GET comments for a post from https://jsonplaceholder.typicode.com/posts/{postId}/comments - the comments must be returned as part of the post.
    //Check AuditionService.getPostWithCommentsById() ...
    //Implemented in service layer as it makes more sense to have repo layer expose raw
    //functionality and stitching / enrichment can then be done by higher / business layer

    // TODO write a method. GET comments for a particular Post from https://jsonplaceholder.typicode.com/comments?postId={postId}.
    // The comments are a separate list that needs to be returned to the API consumers. Hint: this is not part of the AuditionPost pojo.
    @CircuitBreaker(name = "backend", fallbackMethod = "fallbackComments")
    public List<Comment> getComments(final Optional<Integer> postId) {

        try {

            final ResponseEntity<List<Comment>> response = restTemplate.exchange(
                constructGetCommentsUri(postId),
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Comment>>() {
                });

            return response.getBody();

        } catch (final HttpClientErrorException e) {
            throw new SystemException("Error while accessing comments for post " + postId, e.getStatusText(),
                e.getStatusCode().value(), e);
        }
    }

    private URI constructGetCommentsUri(final Optional<Integer> postId) {

        return UriComponentsBuilder.fromUri(
                URI.create(config.getAuditionBaseUri() + config.getAuditionCommentsSuffix()))
            .queryParamIfPresent(config.getAuditionCommentsFilter(), postId)
            .build().toUri();

    }

    @Autowired
    public void setConfig(final AppConfig appConfig) {
        this.config = appConfig;
    }

    public AppConfig getConfig() {
        return config;
    }

    @Autowired
    public void setRestTemplate(final RestTemplate template) {
        this.restTemplate = template;
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public List<Comment> fallbackComments(Exception e) {
        fallbackThrow(e);
        return new ArrayList<>();
    }

    public AuditionPost fallbackPost(Exception e) {
        fallbackThrow(e);
        return null;
    }

    public List<AuditionPost> fallbackPosts(Exception e) {
        fallbackThrow(e);
        return new ArrayList<>();
    }

    public void fallbackThrow(Exception e) {
        throw new SystemException(e.getMessage(), ExceptionControllerAdvice.DOWNSTREAM_UNAVAILABLE,
            HttpStatus.SERVICE_UNAVAILABLE.value(), e);

    }
}

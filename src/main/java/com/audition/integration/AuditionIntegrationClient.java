package com.audition.integration;

import com.audition.common.exception.SystemException;
import com.audition.configuration.AppConfig;
import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class AuditionIntegrationClient {

    /**
     * We could have used Circuit breaker to handle downstream issues. But unable to do this due to time constraints.
     */

    private AppConfig config;
    private RestTemplate restTemplate;

    public List<AuditionPost> getPosts(final Integer userId) {
        // TODO make RestTemplate call to get Posts from https://jsonplaceholder.typicode.com/posts

        String endpoint = config.getAuditionBaseUri() + config.getAuditionPostsSuffix();

        if (null != userId) {
            endpoint = endpoint + config.getAuditionUserIdFilter() + userId;
        }

        final ResponseEntity<List<AuditionPost>> response = restTemplate.exchange(
            endpoint,
            HttpMethod.GET, null, new ParameterizedTypeReference<List<AuditionPost>>() {
            });
        return response.getBody();

    }

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
    public AuditionPost getPostWithCommentsById(final Integer postId) {

        //Assumption here is that we need to return the post object along with list of comments
        //Below could be improved to make the calls parallel using something like CompletableFuture
        final AuditionPost post = getPostById(postId);
        post.setComments(getComments(postId));
        return post;
    }

    // TODO write a method. GET comments for a particular Post from https://jsonplaceholder.typicode.com/comments?postId={postId}.
    // The comments are a separate list that needs to be returned to the API consumers. Hint: this is not part of the AuditionPost pojo.
    public List<Comment> getComments(final Integer postId) {

        String endpoint = config.getAuditionBaseUri() + config.getAuditionCommentsSuffix();

        if (null != postId) {
            endpoint = endpoint + config.getAuditionCommentsFilter() + postId;
        }
        try {

            final ResponseEntity<List<Comment>> response = restTemplate.exchange(
                endpoint,
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Comment>>() {
                });

            return response.getBody();

        } catch (final HttpClientErrorException e) {
            throw new SystemException("Error while accessing comments for post " + postId, e.getStatusText(),
                e.getStatusCode().value(), e);
        }
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
}

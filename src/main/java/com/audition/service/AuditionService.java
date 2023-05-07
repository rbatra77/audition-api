package com.audition.service;

import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditionService {

    private AuditionIntegrationClient auditionIntegrationClient;


    public List<AuditionPost> getPosts(final Optional<Integer> userId, final Optional<String> title) {
        return auditionIntegrationClient.getPosts(userId, title);
    }

    public AuditionPost getPostById(final Integer postId) {
        return auditionIntegrationClient.getPostById(postId);
    }

    public AuditionPost getPostWithCommentsById(final Integer postId) {

        //Assumption here is that we need to return the post object along with list of comments

        final CompletableFuture<AuditionPost> postCompletableFuture = CompletableFuture.supplyAsync(
            () -> auditionIntegrationClient.getPostById(postId));

        final CompletableFuture<List<Comment>> commentsCompletableFuture = CompletableFuture.supplyAsync(
            () -> auditionIntegrationClient.getComments(Optional.of(postId)));

        final CompletableFuture<Void> completableFutureAllOf = CompletableFuture.allOf(
            postCompletableFuture, commentsCompletableFuture);

        final CompletableFuture<AuditionPost> combinedPostCompletableFuture = completableFutureAllOf
            .thenApply(
                (voidInput) -> postCompletableFuture.join().saveComments(commentsCompletableFuture.join()));

        return combinedPostCompletableFuture.join();

    }

    public List<Comment> getComments(final Optional<Integer> postId) {
        return auditionIntegrationClient.getComments(postId);
    }

    @Autowired
    public void setAuditionIntegrationClient(final AuditionIntegrationClient auditionIntegrationClient) {
        this.auditionIntegrationClient = auditionIntegrationClient;
    }

    public AuditionIntegrationClient getAuditionIntegrationClient() {
        return auditionIntegrationClient;
    }

}

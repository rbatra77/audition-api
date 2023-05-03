package com.audition.service;

import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditionService {

    private AuditionIntegrationClient auditionIntegrationClient;


    public List<AuditionPost> getPosts(final Integer userId) {
        return auditionIntegrationClient.getPosts(userId);
    }

    public AuditionPost getPostById(final Integer postId) {
        return auditionIntegrationClient.getPostById(postId);
    }

    public AuditionPost getPostWithCommentsById(final Integer postId) {
        return auditionIntegrationClient.getPostWithCommentsById(postId);
    }

    public List<Comment> getComments(final Integer postId) {
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

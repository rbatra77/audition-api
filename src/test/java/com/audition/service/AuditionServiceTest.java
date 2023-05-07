package com.audition.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionPost;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AuditionServiceTest {

    @Mock
    private AuditionIntegrationClient auditionIntegrationClient;

    @InjectMocks
    private AuditionService auditionService = new AuditionService();

    private List<AuditionPost> posts;

    @BeforeEach
    void setMockitoOutput() {

        posts = new ArrayList<>();

        AuditionPost post1 = new AuditionPost();
        post1.setId(1);
        post1.setBody("Body 1");
        post1.setTitle("Title 1");
        post1.setUserId(1);

        AuditionPost post2 = new AuditionPost();
        post2.setId(2);
        post2.setBody("Body 2");
        post2.setTitle("Title 2");
        post2.setUserId(2);

        posts.add(post1);
        posts.add(post2);

        when(auditionIntegrationClient.getPosts(Optional.empty(), Optional.empty())).thenReturn(posts);
    }

    @Test
    void testPosts() {
        List<AuditionPost> postsReturned = auditionService.getPosts(Optional.empty(), Optional.empty());
        
        assertEquals(posts.size(), postsReturned.size());

        assertEquals(posts.get(0).getId(), postsReturned.get(0).getId());
        assertEquals(posts.get(0).getUserId(), postsReturned.get(0).getUserId());
        assertEquals(posts.get(0).getBody(), postsReturned.get(0).getBody());
        assertEquals(posts.get(0).getTitle(), postsReturned.get(0).getTitle());

        assertEquals(posts.get(1).getId(), postsReturned.get(1).getId());
        assertEquals(posts.get(1).getUserId(), postsReturned.get(1).getUserId());
        assertEquals(posts.get(1).getBody(), postsReturned.get(1).getBody());
        assertEquals(posts.get(1).getTitle(), postsReturned.get(1).getTitle());

    }
}

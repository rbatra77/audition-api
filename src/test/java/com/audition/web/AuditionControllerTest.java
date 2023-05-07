package com.audition.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.audition.common.logging.AuditionLogger;
import com.audition.configuration.AppConfig;
import com.audition.integration.interceptors.LoggingInterceptor;
import com.audition.model.AuditionPost;
import com.audition.service.AuditionService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuditionController.class)
public class AuditionControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuditionService auditionService;

    @MockBean
    private AppConfig config;

    @MockBean
    private LoggingInterceptor interceptor;

    @MockBean
    private AuditionLogger logger;

    private List<AuditionPost> posts;

    private List<AuditionPost> postsWithTitle;

    @BeforeEach
    void setMockOutput() {

        posts = new ArrayList<>();
        postsWithTitle = new ArrayList<>();

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

        postsWithTitle.add(post1);

        when(auditionService.getPosts(Optional.empty(), Optional.empty())).thenReturn(posts);
        when(auditionService.getPosts(Optional.empty(), Optional.of("Title 1"))).thenReturn(postsWithTitle);

    }

    @Test
    public void testPostsWithoutTitle() throws Exception {

        mockMvc.perform(get("/posts")).andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(posts.size()))
            .andExpect(jsonPath("$.[0].id").value(posts.get(0).getId()))
            .andExpect(jsonPath("$.[0].title").value(posts.get(0).getTitle()))
            .andExpect(jsonPath("$.[0].body").value(posts.get(0).getBody()))
            .andExpect(jsonPath("$.[0].userId").value(posts.get(0).getUserId()))
            .andExpect(jsonPath("$.[1].id").value(posts.get(1).getId()))
            .andExpect(jsonPath("$.[1].title").value(posts.get(1).getTitle()))
            .andExpect(jsonPath("$.[1].body").value(posts.get(1).getBody()))
            .andExpect(jsonPath("$.[1].userId").value(posts.get(1).getUserId()));

    }

    @Test
    public void testPostsWithTitle() throws Exception {

        mockMvc.perform(get("/posts?title=Title 1")).andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(postsWithTitle.size()))
            .andExpect(jsonPath("$.[0].id").value(postsWithTitle.get(0).getId()))
            .andExpect(jsonPath("$.[0].title").value(postsWithTitle.get(0).getTitle()))
            .andExpect(jsonPath("$.[0].body").value(postsWithTitle.get(0).getBody()))
            .andExpect(jsonPath("$.[0].userId").value(postsWithTitle.get(0).getUserId()));

    }

    @Test
    public void testPostsWithHTMLTitle() throws Exception {

        mockMvc.perform(get("/posts?title=<ss>Title 1</ss>")).andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(postsWithTitle.size()))
            .andExpect(jsonPath("$.[0].id").value(postsWithTitle.get(0).getId()))
            .andExpect(jsonPath("$.[0].title").value(postsWithTitle.get(0).getTitle()))
            .andExpect(jsonPath("$.[0].body").value(postsWithTitle.get(0).getBody()))
            .andExpect(jsonPath("$.[0].userId").value(postsWithTitle.get(0).getUserId()));

    }

    @Test
    public void testPostsWithUnavailableTitle() throws Exception {

        mockMvc.perform(get("/posts?title=Title 12345")).andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));

    }
}

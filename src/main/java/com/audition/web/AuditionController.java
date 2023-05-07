package com.audition.web;

import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import com.audition.service.AuditionService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Optional;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class AuditionController {

    private AuditionService auditionService;

    // TODO Add a query param that allows data filtering. The intent of the filter is at developers discretion.
    @RequestMapping(value = "/posts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<AuditionPost> getPosts(
        @RequestParam(value = "userId", required = false) @Min(0) final Integer userId,
        @RequestParam(value = "title", required = false) @Size(min = 1, max = 100) final String title) {

        // TODO Add logic that filters response data based on the query param

        //We would leverage the filtering feature of backend as it already supports this.
        //So we don't have to duplicate the functionality.

        Optional<Integer> sanitisedUserId = null != userId ? Optional.of(userId) : Optional.empty();

        //The below cleans HTML tags. We don't have any sql DB as such
        //But depending on org standards, we can further sanitise it for SQL queries etc.
        Optional<String> sanitisedTitle =
            null != title ? Optional.of(Jsoup.clean(title, Safelist.none())) : Optional.empty();

        return auditionService.getPosts(sanitisedUserId, sanitisedTitle);
    }

    @RequestMapping(value = "/posts/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody AuditionPost getPostById(@PathVariable("id") @Min(0) final Integer postId) {

        return auditionService.getPostById(postId);

        // TODO Add input validation

        //Numeric field/params will have limited validations .. some of them are defined above and below ..
        //String validation and sanitization (to remove html blocks) has been done above

    }

    // TODO Add additional methods to return comments for each post. Hint: Check https://jsonplaceholder.typicode.com/

    @RequestMapping(value = "/posts/{id}/comments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody AuditionPost getPostsWithComments(@PathVariable("id") @Positive final Integer postId) {

        return auditionService.getPostWithCommentsById(postId);

        // TODO Add input validation
        // Implemented where needed

    }

    @RequestMapping(value = "/comments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Comment> getCommentsByPostId(
        @RequestParam(value = "postId", required = false) @Positive final Integer postId) {

        Optional<Integer> sanitisedPostId = null != postId ? Optional.of(postId) : Optional.empty();

        return auditionService.getComments(sanitisedPostId);
    }

    @Autowired
    public void setAuditionService(final AuditionService auditionService) {
        this.auditionService = auditionService;
    }

    public AuditionService getAuditionService() {
        return auditionService;
    }

}

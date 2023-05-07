package com.audition.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuditionPost {

    //Modelling can be improved to include parent/child relationships

    private int userId;
    private int id;
    private String title;
    private String body;

    private List<Comment> comments;

    public AuditionPost saveComments(List<Comment> comments) {
        this.comments = comments;
        return this;
    }

}

package com.audition.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Comment {

    //Modelling can be improved to include parent/child relationships

    private int id;
    private String name;
    private String email;
    private String body;
    private int postId;

}

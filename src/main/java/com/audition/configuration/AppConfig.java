package com.audition.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("app.config")
@Getter
@Setter
public class AppConfig {

    private int connectTimeout;
    private int readTimeout;
    private String auditionBaseUri;
    private String auditionPostsSuffix;
    private String auditionCommentsSuffix;
    private String auditionCommentsFilter;
    private String auditionUserIdFilter;
    private String auditionTitleFilter;

}

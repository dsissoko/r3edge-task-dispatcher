package com.r3edge.tasks.dispatcher;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
@RefreshScope
@ConfigurationProperties(prefix = "r3edge.tasks")
public class TaskConfiguration {

    private List<Task> definitions = new ArrayList<>();

    @PostConstruct
    public void postConstruct() {
        log.debug("Tasks configuration chargée avec {} tasks", definitions.size());
    }
}

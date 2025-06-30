package com.r3edge.task.dispatcher;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "tasks")
@RefreshScope
public class TaskConfiguration {

    private List<Task> definitions = new ArrayList<>();

    public List<Task> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(List<Task> definitions) {
        this.definitions = definitions;
    }
}

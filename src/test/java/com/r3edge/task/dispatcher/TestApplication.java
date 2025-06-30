package com.r3edge.task.dispatcher;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

@SpringBootApplication
@Configuration
@Import(TaskConfiguration.class)
public class TestApplication {

    @Bean
    public TaskHandlerRegistry taskHandlerRegistry(List<TaskHandler> handlers) {
        return new TaskHandlerRegistry(handlers);
    }
}

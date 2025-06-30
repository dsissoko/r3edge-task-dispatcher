package com.r3edge.task.dispatcher;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DispatcherIntegrationTest {

    @Autowired
    private TaskDispatcher dispatcher;

    @Test
    void shouldDispatchTasksFromYaml() throws Exception {
        try (InputStream input = getClass().getResourceAsStream("/tasks-test.yml")) {
            List<Task> tasks = YamlTaskLoader.loadTasks(input);
            assertThat(tasks).isNotEmpty();

            for (Task task : tasks) {
                dispatcher.dispatch(task);
            }
        }
    }
}

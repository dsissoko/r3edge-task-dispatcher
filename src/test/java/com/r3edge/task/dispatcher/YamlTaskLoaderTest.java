package com.r3edge.task.dispatcher;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class YamlTaskLoaderTest {

    @Test
    void shouldLoadTasksFromValidYaml() throws Exception {
        // Arrange
        String yamlContent = "- id: task-1\n  type: test-type\n  meta:\n    key: value";
        InputStream inputStream = new ByteArrayInputStream(yamlContent.getBytes(StandardCharsets.UTF_8));

        // Act
        List<Task> tasks = YamlTaskLoader.loadTasks(inputStream);

        // Assert
        assertThat(tasks).hasSize(1);
        Task task = tasks.get(0);
        assertThat(task.getId()).isEqualTo("task-1");
        assertThat(task.getType()).isEqualTo("test-type");
        assertThat(task.getMeta()).containsEntry("key", "value");
    }

    @Test
    void shouldReturnEmptyListForEmptyYamlFile() throws Exception {
        // Arrange
        String yamlContent = "---"; // A valid but empty YAML document
        InputStream inputStream = new ByteArrayInputStream(yamlContent.getBytes(StandardCharsets.UTF_8));

        // Act
        List<Task> tasks = YamlTaskLoader.loadTasks(inputStream);

        // Assert
        assertThat(tasks).isEmpty();
    }

    @Test
    void shouldThrowExceptionForMalformedYaml() {
        // Arrange
        String yamlContent = "- id: task-1\n  type: test-type\n  meta: \n    key: value\n- id: task-2\n  type: another-type\n  meta: value2"; // Malformed YAML
        InputStream inputStream = new ByteArrayInputStream(yamlContent.getBytes(StandardCharsets.UTF_8));

        // Act & Assert
        assertThatThrownBy(() -> YamlTaskLoader.loadTasks(inputStream))
                .isInstanceOf(Exception.class);
    }
}

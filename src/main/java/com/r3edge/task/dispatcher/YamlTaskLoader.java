package com.r3edge.task.dispatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Utilitaire pour charger les tâches depuis un fichier YAML.
 */
public class YamlTaskLoader {

    private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());

    public static List<Task> loadTasks(InputStream input) throws Exception {
        Task[] tasks = MAPPER.readValue(input, Task[].class);
        return Arrays.asList(tasks);
    }
}

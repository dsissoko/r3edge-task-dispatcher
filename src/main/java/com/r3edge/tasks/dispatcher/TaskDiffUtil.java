package com.r3edge.tasks.dispatcher;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

public class TaskDiffUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    @SneakyThrows
    public static boolean areTasksEqual(Task t1, Task t2) {
        JsonNode n1 = mapper.valueToTree(t1);
        JsonNode n2 = mapper.valueToTree(t2);
        return n1.equals(n2);
    }
}

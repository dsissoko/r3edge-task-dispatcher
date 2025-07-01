package com.r3edge.tasks.dispatcher;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

/**
 * Utilitaire permettant de comparer deux tâches sur la base de leur représentation JSON.
 */
public class TaskDiffUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Compare deux instances de {@link Task} en utilisant leur représentation JSON.
     *
     * @param t1 la première tâche
     * @param t2 la seconde tâche
     * @return {@code true} si les deux tâches sont équivalentes, {@code false} sinon
     */
    @SneakyThrows
    public static boolean areTasksEqual(Task t1, Task t2) {
        JsonNode n1 = mapper.valueToTree(t1);
        JsonNode n2 = mapper.valueToTree(t2);
        return n1.equals(n2);
    }
}

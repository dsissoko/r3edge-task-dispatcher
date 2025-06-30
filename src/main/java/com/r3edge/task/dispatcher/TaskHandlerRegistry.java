package com.r3edge.task.dispatcher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registre des TaskHandler disponibles dans le contexte Spring.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskHandlerRegistry {

    private final Map<String, TaskHandler> handlerMap = new HashMap<>();

    public TaskHandlerRegistry(List<TaskHandler> handlers) {
        for (TaskHandler handler : handlers) {
            handlerMap.put(handler.getType(), handler);
            log.info("Handler enregistré pour le type : {}", handler.getType());
        }
    }

    /**
     * Récupère un handler pour un type de tâche donné.
     */
    public TaskHandler getHandler(String type) {
        return handlerMap.get(type);
    }

    /**
     * Enregistre dynamiquement un nouveau handler.
     */
    public void addHandler(TaskHandler handler) {
        handlerMap.put(handler.getType(), handler);
        log.info("Handler ajouté dynamiquement pour le type : {}", handler.getType());
    }
}

package com.r3edge.task.dispatcher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Composant central chargé de déléguer l'exécution d'une tâche au handler approprié.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskDispatcher {

    private final TaskHandlerRegistry registry;

    /**
     * Dispatch une tâche en la déléguant au handler correspondant à son type.
     */
    public void dispatch(Task task) {
        if (!task.isEnabled()) {
            log.info("La tâche {} est désactivée, elle ne sera pas exécutée.", task.getId());
            return;
        }

        TaskHandler handler = registry.getHandler(task.getType());
        if (handler == null) {
            throw new TaskExecutionException("No handler found for task type: " + task.getType());
        }

        try {
            log.info("Exécution de la tâche {} avec le handler {}", task.getId(), handler.getClass().getSimpleName());
            handler.handle(task);
        } catch (Exception e) {
            log.error("Erreur lors de l'exécution de la tâche {}", task.getId(), e);
            throw new TaskExecutionException("Failed to execute task " + task.getId(), e);
        }
    }
}

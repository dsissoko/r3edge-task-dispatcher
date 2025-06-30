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
        TaskHandler handler = registry.getHandler(task.getType());
        if (handler == null) {
            log.warn("Aucun handler trouvé pour le type de tâche : {}", task.getType());
            return;
        }

        try {
            log.info("Exécution de la tâche {} avec le handler {}", task.getId(), handler.getClass().getSimpleName());
            handler.handle(task);
        } catch (Exception e) {
            log.error("Erreur lors de l'exécution de la tâche {}", task.getId(), e);
        }
    }
}

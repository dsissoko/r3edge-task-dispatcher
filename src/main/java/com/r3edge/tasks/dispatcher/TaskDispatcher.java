package com.r3edge.tasks.dispatcher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import org.springframework.stereotype.Component;

/**
 * Composant central chargé de déléguer l'exécution d'une tâche au handler approprié.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskDispatcher {

    private final TaskHandlerRegistry registry;
    private final DefaultTaskExecutor defaultTaskExecutor;
    private final Optional<LockingTaskExecutor> lockingTaskExecutor;

    /**
     * Dispatch une tâche en la déléguant au handler correspondant à son type.
     *
     * @param task la tâche à exécuter
     */
    public void dispatch(Task task) {
        if (!task.isEnabled()) {
            log.info("La tâche {} est désactivée, elle ne sera pas exécutée.", task.getId());
            return;
        }

        TaskHandler handler = registry.getHandler(task.getType());
        if (handler == null) {
            log.warn("⚠️ Aucun handler trouvé pour le type '{}', tâche {} ignorée", task.getType(), task.getId());
            throw new TaskExecutionException("No handler found for task type: " + task.getType());
        }

        try {
            log.info("Exécution de la tâche {} avec le handler {}", task.getId(), handler.getClass().getSimpleName());

            if (task.isDistributedLock()) {
                if (lockingTaskExecutor.isPresent()) {
                    lockingTaskExecutor.get().execute(task, handler);
                } else {
                    throw new TaskExecutionException("Distributed lock requested but no LockProvider configured for task " + task.getId());
                }
            } else {
                defaultTaskExecutor.execute(task, handler);
            }
        } catch (Exception e) {
            log.error("Erreur lors de l'exécution de la tâche {}", task.getId(), e);
            throw new TaskExecutionException("Failed to execute task " + task.getId(), e);
        }
    }
}

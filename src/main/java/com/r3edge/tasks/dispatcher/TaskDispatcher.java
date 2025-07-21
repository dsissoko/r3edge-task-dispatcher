package com.r3edge.tasks.dispatcher;

import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Composant central chargé de déléguer l'exécution d'une tâche au handler
 * approprié.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskDispatcher implements ApplicationListener<WebServerInitializedEvent> {

	private final TaskHandlerRegistry registry;
	private final DefaultTaskExecutor defaultTaskExecutor;
	private final TaskConfiguration taskConfiguration;

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
			defaultTaskExecutor.execute(task, handler);
		} catch (Exception e) {
			log.error("Erreur lors de l'exécution de la tâche {}", task.getId(), e);
			throw new TaskExecutionException("Failed to execute task " + task.getId(), e);
		}
	}

	@Override
	public void onApplicationEvent(WebServerInitializedEvent event) {
		taskConfiguration.getDefinitions().forEach(t -> {
			log.debug("Lancement automatique de {}", t);
			dispatch(t);
		});
	}
}

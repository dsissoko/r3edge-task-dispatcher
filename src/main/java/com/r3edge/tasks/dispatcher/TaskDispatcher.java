package com.r3edge.tasks.dispatcher;

import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
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
public class TaskDispatcher {

	private final TaskHandlerRegistry registry;
	private final DefaultTaskExecutor defaultTaskExecutor;
	@Lazy
	private final ITaskScheduler taskScheduler;
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
			//throw new TaskExecutionException("No handler found for task type: " + task.getType());
			return;
		}
		
        String cron = task.getCron();
        if (cron != null && taskScheduler != null) {
        	log.info("Planification de la tâche {} avec le handler {} et le scheduler {}", task.getId(), handler.getClass().getSimpleName(), taskScheduler);
            taskScheduler.schedule(task,() -> dispatch(task));
            return;
        }		

		try {
			log.info("Exécution de la tâche {} avec le handler {}", task.getId(), handler.getClass().getSimpleName());
			defaultTaskExecutor.execute(task, handler);
		} catch (Exception e) {
			log.error("Erreur lors de l'exécution de la tâche {}", task.getId(), e);
			//throw new TaskExecutionException("Failed to execute task " + task.getId(), e);
		}
	}

	@EventListener(WebServerInitializedEvent.class)
	public void onApplicationEvent(WebServerInitializedEvent event) {
		taskConfiguration.getDefinitions().forEach(t -> {
			log.debug("Lancement automatique de {}", t);
			dispatch(t);
		});
	}
	
	@EventListener(RefreshScopeRefreshedEvent.class)
	public void onRefreshEvent(RefreshScopeRefreshedEvent event) {
		log.info("📥 Refresh détecté : redéclenchement des tâches.");
		taskConfiguration.getDefinitions().forEach(t -> {
			log.debug("Redispatch de la tâche {}", t);
			dispatch(t);
		});
	}
}

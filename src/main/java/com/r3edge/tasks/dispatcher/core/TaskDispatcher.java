package com.r3edge.tasks.dispatcher.core;

import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Composant central chargé de dispatcher les tâches configurées vers
 * les exécutants ou planificateurs adéquats, en fonction de leur stratégie.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskDispatcher {

	private final TaskHandlerRegistry registry;
	private final TaskConfiguration taskConfiguration;
	private final TaskStrategyRouter strategyRouter;

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

		TaskHandler handler = registry.getHandler(task.getType()).orElse(null);
		if (handler == null) {
			log.warn("⚠️ Aucun handler trouvé pour le type '{}', tâche {} ignorée", task.getType(), task.getId());
			return;
		}

		String strategy = task.getStrategy();
		// Resolution dynamique
		ITaskExecutor executor = strategyRouter.resolveExecutor(task);
		ITaskScheduler scheduler = strategyRouter.resolveScheduler(task);

		String cron = task.getCron();
		if (cron != null && !cron.isBlank()) {
			log.debug("Dispatch CRON de la tâche {} avec le handler {} via stratégie '{}'", task.getId(),
					handler.getClass().getSimpleName(), strategy);
			scheduler.schedule(task, handler);
			return;
		}

		try {
			log.debug("Dispatch EXECUTOR de la tâche {} avec le handler {} via stratégie '{}'", task.getId(),
					handler.getClass().getSimpleName(), strategy);
			executor.execute(task, handler);
		} catch (Exception e) {
			log.error("💥 Erreur lors de l'exécution de la tâche {}", task.getId(), e);
		}
	}

	/**
	 * Événement déclenché au démarrage du serveur web. Permet de déclencher
	 * automatiquement le dispatch des tâches configurées.
	 *
	 * @param event l'événement de démarrage du serveur
	 */
	@EventListener(WebServerInitializedEvent.class)
	public void onApplicationEvent(WebServerInitializedEvent event) {
		log.info("🔄 Démarrage du service de dispatch.");
		taskConfiguration.getDefinitions().forEach(this::dispatch);
	}

	/**
	 * Événement déclenché après un rafraîchissement de configuration. Permet de
	 * re-dispatcher les tâches en fonction des nouvelles définitions ou cron
	 * modifiés.
	 * 
	 * @param event L'événement de rafraîchissement du scope.
	 */
	@EventListener(RefreshScopeRefreshedEvent.class)
	public void onRefreshEvent(RefreshScopeRefreshedEvent event) {
		refreshTasks();
	}

    /**
     * Recharge l'ensemble des tâches à partir de la configuration actuelle.
     */
	public void refreshTasks() {
		log.info("🔁 Refresh complet des tâches...");
		cleanupObsoleteTasks();
		taskConfiguration.getDefinitions().forEach(this::dispatch);
		log.info("✅ Refresh terminé.");
	}

    /**
     * Supprime toutes les tâches actives planifiées ou en cours d'exécution.
     */
	public void cleanupObsoleteTasks() {
		log.info("🧹 CleanUp des tâches actives...");
		strategyRouter.allSchedulers().forEach(scheduler -> {
			for (String taskId : scheduler.getScheduledTaskIds()) {
				scheduler.unscheduleById(taskId);
			}
		});
		strategyRouter.allExecutors().forEach(executor -> {
			for (String taskId : executor.getExecutedTaskIds()) {
				Task fake = new Task(); // ⚠️ à remplacer par withId(...) si possible
				fake.setId(taskId);
				executor.cancel(fake);
			}
		});
		log.info("✅ CleanUp terminé.");
	}

}

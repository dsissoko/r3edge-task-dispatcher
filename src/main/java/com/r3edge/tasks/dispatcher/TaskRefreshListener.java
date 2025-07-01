package com.r3edge.tasks.dispatcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Écouteur Spring déclenché lors du rafraîchissement du scope {@code @RefreshScope}.
 * Permet de redéclencher ou mettre à jour dynamiquement certaines tâches si nécessaire.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskRefreshListener implements ApplicationListener<RefreshScopeRefreshedEvent> {

	private final TaskConfiguration configuration;
	private final TaskHandlerRegistry registry;
	private final TaskDispatcher dispatcher;

	private final Map<String, Task> lastSeen = new HashMap<>();

	@Override
	public void onApplicationEvent(RefreshScopeRefreshedEvent event) {
		List<Task> current = configuration.getDefinitions();
		Map<String, Task> currentById = new HashMap<>();
		current.forEach(task -> currentById.put(task.getId(), task));

		// Diff : nouveaux ou modifiés
		for (Task updated : current) {
			Task previous = lastSeen.get(updated.getId());
			currentById.put(updated.getId(), updated);

			if (previous == null) {
				// Nouvelle tâche
				if (updated.isHotReload() && updated.isEnabled()) {
					dispatcher.dispatch(updated);
				}
			} else if (!TaskDiffUtil.areTasksEqual(previous, updated)) {
				TaskHandler handler = registry.getHandler(updated.getType());
				if (handler != null) {
					handler.onTaskReload(previous, updated, false);
				}
			}
		}

		// Diff : supprimées
		for (String oldId : lastSeen.keySet()) {
			if (!currentById.containsKey(oldId)) {
				Task deleted = lastSeen.get(oldId);
				if (deleted.isHotReload()) {
					TaskHandler handler = registry.getHandler(deleted.getType());
					if (handler != null) {
						handler.onTaskReload(deleted, null, true);
					}
				}
			}
		}

		// Mise à jour de la mémoire
		lastSeen.clear();
		current.forEach(task -> lastSeen.put(task.getId(), task));
	}
}

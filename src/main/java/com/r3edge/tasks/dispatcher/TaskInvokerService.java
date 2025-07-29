package com.r3edge.tasks.dispatcher;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * Service chargé d'invoquer dynamiquement les handlers de tâches.
 */
@Service
@RequiredArgsConstructor
public class TaskInvokerService {

	private final TaskHandlerRegistry registry;

	/**
	 * Invoque le handler approprié pour la tâche donnée.
	 * Si le handler fournit une lambda, celle-ci est retournée pour une exécution différée.
	 * Sinon, la tâche est exécutée immédiatement.
	 *
	 * @param task La tâche à invoquer.
	 * @return Une lambda exécutable différée, ou {@code null} si la tâche a été exécutée immédiatement.
	 * @throws IllegalStateException si aucun handler n'est trouvé.
	 */
	public TaskLambda toLambda(Task task) {
		TaskHandler handler = registry.getHandler(task.getType()).orElse(null);
		if (handler == null) {
			throw new IllegalStateException("Aucun handler pour le type de tâche : " + task.getType());
		}

		TaskLambda lambda = handler.toLambda(task);
		if (lambda == null) {
			throw new IllegalStateException("Le handler " + handler.getClass().getSimpleName()
					+ " ne fournit pas de lambda pour : " + task.getType());
		}

		return lambda;
	}

	/**
	 * Exécute immédiatement une tâche via son handler.
	 *
	 * @param task la tâche à exécuter
	 */
	public void invokeNow(Task task) {
		TaskHandler handler = registry.getHandler(task.getType()).orElse(null);
		if (handler == null) {
			throw new IllegalStateException("Aucun handler pour le type de tâche : " + task.getType());
		}
		handler.handle(task);
	}
}

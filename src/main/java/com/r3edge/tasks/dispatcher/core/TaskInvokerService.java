package com.r3edge.tasks.dispatcher.core;

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
	 * Exécute immédiatement une tâche.
	 * 
	 * @param task   La tâche à exécuter.
	 */
	public void execute(TaskDescriptor task) {
		// Résolution du handler en fonction du type de tâche
		TaskHandler handler = registry.getHandler(task.getHandler()).orElseThrow(
				() -> new IllegalStateException("Aucun handler pour la tâche: " + task.getId() +", handler: " + task.getHandler()));
		// Toujours instrumentation (pipeline listener)
		Runnable r = createRunnable(task, handler);
		r.run();
	}

	/**
	 * Crée un Runnable instrumenté pour la tâche et le logger fournis.
	 * 
	 * @param task   Le descriptor de la tâche à exécuter.
	 * @return Un Runnable prêt à exécuter la tâche.
	 */
	public Runnable createRunnable(TaskDescriptor task) {
		TaskHandler handler = registry.getHandler(task.getHandler()).orElseThrow(
				() -> new IllegalStateException("Aucun handler pour le type de tâche : " + task.getHandler()));
		return createRunnable(task, handler);
	}

	// Version privée conservée pour factorisation interne
	private Runnable createRunnable(TaskDescriptor task, TaskHandler handler) {
		return () -> {
			try {
				handler.execute(task);
			} catch (Throwable e) {
				sneakyThrow(e);
			}
		};
	}

	/**
	 * Lance une exception checked sans avoir à la déclarer.
	 *
	 * @param e   exception à lancer
	 * @param <E> type de l'exception
	 * @throws E toujours levée, jamais capturée
	 */
	@SuppressWarnings("unchecked")
	static <E extends Throwable> void sneakyThrow(Throwable e) throws E {
		throw (E) e;
	}
}

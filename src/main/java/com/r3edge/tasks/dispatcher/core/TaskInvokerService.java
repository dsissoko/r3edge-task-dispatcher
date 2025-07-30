package com.r3edge.tasks.dispatcher.core;

import org.slf4j.Logger;
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
	 * Exécute immédiatement une tâche via son handler déduit
	 *
	 * @param task la tâche à exécuter
	 */
	public void invokeNow(Task task) {
		TaskHandler handler = registry.getHandler(task.getType()).orElse(null);
		invokeNow(task, handler);
	}
	
    /**
     * Exécute immédiatement une tâche via un handler spécifique.
     *
     * @param task la tâche à exécuter
     * @param handler le handler à utiliser
     */
	public void invokeNow(Task task, TaskHandler handler) {
		if (handler == null) {
			throw new IllegalStateException("Aucun handler pour le type de tâche : " + task.getType());
		}
		handler.handle(task);
	}
	
	/**
	 * Exécute immédiatement une tâche avec un logger contextuel.
	 *
	 * @param task la tâche à exécuter
	 * @param logger le logger à utiliser
	 */
	public void invokeNow(Task task, Logger logger) {
		TaskHandler handler = registry.getHandler(task.getType()).orElse(null);
		invokeNow(task, handler, logger);
	}

	/**
	 * Exécute immédiatement une tâche avec un handler et un logger spécifique.
	 *
	 * @param task la tâche à exécuter
	 * @param handler le handler à utiliser
	 * @param logger le logger contextuel à utiliser
	 */
	public void invokeNow(Task task, TaskHandler handler, Logger logger) {
		if (handler == null) {
			throw new IllegalStateException("Aucun handler pour le type de tâche : " + task.getType());
		}
		handler.handle(task, logger);
	}

	/**
	 * Construit un Runnable prêt à exécuter la tâche, en utilisant le handler et le listener fournis.
	 *
	 * @param task la tâche à exécuter
	 * @param handler le handler à utiliser
	 * @param listener le listener d’exécution
	 * @param logger le logger contextuel
	 * @return un Runnable encapsulant l’exécution de la tâche
	 */
	public Runnable createRunnable(Task task, TaskHandler handler, ITaskExecutionListener listener, Logger logger) {
		return () -> {
			try {
				listener.onStart(task, logger);
				handler.handle(task, logger);
				listener.onSuccess(task, logger);
			} catch (Throwable e) {
				listener.onFailure(task, e, logger);
				sneakyThrow(e);
			}
		};
	}
	
    /**
     * Lance une exception checked sans avoir à la déclarer.
     *
     * @param e exception à lancer
     * @param <E> type de l'exception
     * @throws E toujours levée, jamais capturée
     */
    @SuppressWarnings("unchecked")
	static <E extends Throwable> void sneakyThrow(Throwable e) throws E {
	    throw (E) e;
	}
	
	
}

package com.r3edge.tasks.dispatcher.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Registre des TaskHandler disponibles dans le contexte Spring.
 */
@Slf4j
@Component
public class TaskHandlerRegistry {

	private final Map<String, TaskHandler> handlerMap = new HashMap<>();

	/**
	 * Initialise le registre avec la liste des handlers présents dans le contexte Spring.
	 *
	 * @param handlers liste des gestionnaires de tâches à enregistrer
	 */
	public TaskHandlerRegistry(List<TaskHandler> handlers) {
		for (TaskHandler handler : handlers) {
			handlerMap.put(handler.getName(), handler);
			log.info("Handler enregistré : {}", handler.getName());
		}
	}

	/**
	 * Récupère un handler pour un type de tâche donné.
	 *
	 * @param name le nom du handler de tâche.
	 * @return le handler correspondant, ou {@code null} si aucun n'est trouvé
	 */
	public Optional<TaskHandler> getHandler(String name) {
	    return Optional.ofNullable(handlerMap.get(name));
	}

	/**
	 * Enregistre dynamiquement un nouveau handler.
	 *
	 * @param handler le gestionnaire de tâche à enregistrer
	 */
	public void addHandler(TaskHandler handler) {
		handlerMap.put(handler.getName(), handler);
		log.info("Handler ajouté dynamiquement : {}", handler.getName());
	}
}

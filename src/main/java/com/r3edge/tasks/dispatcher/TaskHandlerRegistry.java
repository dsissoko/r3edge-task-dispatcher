package com.r3edge.tasks.dispatcher;

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
			handlerMap.put(handler.getType(), handler);
			log.info("Handler enregistré pour le type : {}", handler.getType());
		}
	}

	/**
	 * Récupère un handler pour un type de tâche donné.
	 *
	 * @param type le type de tâche
	 * @return le handler correspondant, ou {@code null} si aucun n'est trouvé
	 */
	public Optional<TaskHandler> getHandler(String type) {
	    return Optional.ofNullable(handlerMap.get(type));
	}

	/**
	 * Enregistre dynamiquement un nouveau handler.
	 *
	 * @param handler le gestionnaire de tâche à enregistrer
	 */
	public void addHandler(TaskHandler handler) {
		handlerMap.put(handler.getType(), handler);
		log.info("Handler ajouté dynamiquement pour le type : {}", handler.getType());
	}
}

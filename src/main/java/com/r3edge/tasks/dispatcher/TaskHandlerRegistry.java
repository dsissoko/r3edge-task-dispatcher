package com.r3edge.tasks.dispatcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Registre des TaskHandler disponibles dans le contexte Spring.
 */
@Slf4j
@Component
public class TaskHandlerRegistry {

	private final Map<String, TaskHandler> handlerMap = new HashMap<>();

	public TaskHandlerRegistry(List<TaskHandler> handlers) {
		for (TaskHandler handler : handlers) {
			handlerMap.put(handler.getType(), handler);
			log.info("Handler enregistré pour le type : {}", handler.getType());
		}
	}

	/**
	 * Récupère un handler pour un type de tâche donné.
	 */
	public TaskHandler getHandler(String type) {
		return handlerMap.get(type);
	}

	/**
	 * Enregistre dynamiquement un nouveau handler.
	 */
	public void addHandler(TaskHandler handler) {
		handlerMap.put(handler.getType(), handler);
		log.info("Handler ajouté dynamiquement pour le type : {}", handler.getType());
	}
}

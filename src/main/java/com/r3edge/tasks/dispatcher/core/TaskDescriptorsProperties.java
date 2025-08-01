package com.r3edge.tasks.dispatcher.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.event.EventListener;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Représente la configuration des tâches chargée dynamiquement
 * via Spring Cloud Config ou des fichiers YAML locaux.
 */
@Getter
@Setter
@Slf4j
@RefreshScope
@ConfigurationProperties(prefix = "r3edge.tasks")
public class TaskDescriptorsProperties {
	
	/**
	 * Si vrai, les tâches dont la date 'at' est dépassée seront ignorées/skippées.
	 * Si faux, elles seront exécutées immédiatement (comportement JobRunr natif).
	 */
	private boolean skipLateTasks = false; // false par défaut pour compatibilité JobRunr
    private List<TaskDescriptor> definitions = new ArrayList<>();

    /**
     * Méthode appelée automatiquement après l’initialisation du bean Spring.
     * Peut être utilisée pour valider ou ajuster la configuration chargée.
     */
    @PostConstruct
    public void init() {
        // Log du chargement des tâches
        log.debug("Tasks configuration chargée avec {} tasks", definitions.size());

    }
    
    /**
     * Méthode appelée automatiquement après un rafraîchissement du contexte
     * provoqué par un {@link RefreshScopeRefreshedEvent}.
     * Permet de logguer ou de déclencher des actions associées à la mise à jour.
     */
    @EventListener(RefreshScopeRefreshedEvent.class)
    public void onRefresh() {
        log.debug("Configuration rafraîchie : {} tâches", definitions.size());
    }
    
    @Override
    public String toString() {
        return "TaskDescriptorsProperties{" +
                "definitions=" + definitions +
                '}';
    }

    /**
     * Retourne les métadonnées associées à une tâche, identifiée par son handler.
     *
     * @param type le nom du handler de la tâche recherchée
     * @return les métadonnées de la tâche ou null si non trouvée
     */
	public Map<String, String> getMetaForTask(String type) {
	    if (type == null) return null;
	    return definitions.stream()
	            .filter(task -> type.equals(task.getHandler()))
	            .findFirst()
	            .map(TaskDescriptor::getMeta)
	            .orElse(null);
	}
}

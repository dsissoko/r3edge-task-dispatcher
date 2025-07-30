package com.r3edge.tasks.dispatcher.core;

import java.util.ArrayList;
import java.util.List;

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
public class TaskConfiguration {
	
    private List<Task> definitions = new ArrayList<>();

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
        return "TaskConfiguration{" +
                "definitions=" + definitions +
                '}';
    }
}

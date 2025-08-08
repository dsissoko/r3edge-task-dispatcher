package com.r3edge.tasks.dispatcher.core;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Représente une tâche générique définie par un identifiant, un handler et des métadonnées.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDescriptor {
    private String id;
    private String handler;
    @Builder.Default
    private String configHandler = null;    
    @Builder.Default
    private boolean enabled = true;
    @Builder.Default
    private String strategy = "default";
    @Builder.Default
    private String cron = null;
    @Builder.Default
    private Instant at = null;
    @Builder.Default
    private boolean redispatchedOnRefresh= false; 
    private Map<String, Object> meta;
    
    
    /**
     * Retourne uniquement les métadonnées dont la valeur est une chaîne.
     *
     * Cette méthode est maintenue pour assurer la compatibilité avec l'ancien code
     * qui attend une {@code Map<String, String>}.
     *
     * @return une {@code Map<String, String>} contenant uniquement les métadonnées
     *         dont la valeur est une chaîne, ou {@code null} si {@code meta} est null
     */
    public Map<String, String> getMetaForTaskAsString() {
        if (meta == null) return null;
        // filtrer uniquement les entrées dont la valeur est String
        return meta.entrySet().stream()
                .filter(e -> e.getValue() instanceof String)
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    e -> (String) e.getValue()
                ));
    }
}

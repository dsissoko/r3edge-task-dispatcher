package com.r3edge.tasks.dispatcher;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Map;

/**
 * Représente une tâche générique définie par un identifiant, un type et des métadonnées.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
    private String id;
    private String type;
    @Builder.Default
    private boolean enabled = true;
    @Builder.Default
    private boolean hotReload = false;
    private Map<String, Object> meta;
}

package com.r3edge.task.dispatcher;

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
    private Map<String, Object> meta;
}

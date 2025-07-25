package com.r3edge.tasks.dispatcher;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String strategy = "default";
    @Builder.Default
    private String cron = null;
    @Builder.Default
    private LocalDateTime at = null;
    private Map<String, Object> meta;
}

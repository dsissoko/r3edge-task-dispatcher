package com.r3edge.tasks.dispatcher.core;

import java.time.Instant;
import java.util.Map;

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
    private boolean enabled = true;
    @Builder.Default
    private String strategy = "default";
    @Builder.Default
    private String cron = null;
    @Builder.Default
    private Instant at = null;
    @Builder.Default
    private boolean redispatchedOnRefresh= false; 
    private Map<String, String> meta;
}

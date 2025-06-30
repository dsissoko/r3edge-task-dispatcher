package com.r3edge.task.dispatcher;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * Représente une tâche générique définie par un identifiant, un type et des métadonnées.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    private String id;
    private String type;
    private Map<String, Object> meta;
}

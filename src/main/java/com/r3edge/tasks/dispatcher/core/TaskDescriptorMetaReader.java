package com.r3edge.tasks.dispatcher.core;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.yaml.snakeyaml.Yaml;

/**
 * Utilitaire statique pour faciliter la lecture des métadonnées dans un {@link TaskDescriptor}.
 * 
 * Propose des méthodes pour récupérer les valeurs sous forme brute (Object), String, Map,
 * ou bien une structure YAML (via SnakeYAML) pour les métadonnées complexes.
 * 
 * La conversion finale vers un type métier spécifique reste à la charge du développeur.
 */
public class TaskDescriptorMetaReader {

    private static final Yaml YAML_PARSER = new Yaml();

    private TaskDescriptorMetaReader() {
        // Utilitaire statique, interdit l'instanciation
    }

    // Méthodes privées manipulant directement la Map meta ----------------------------------

    /**
     * Récupère la valeur brute associée à la clé, si présente.
     *
     * @param meta la map des métadonnées
     * @param key la clé à rechercher
     * @return un Optional contenant la valeur brute, ou empty si absente ou meta null
     */
    private static Optional<Object> getAsObject(Map<String, Object> meta, String key) {
        if (meta == null) return Optional.empty();
        return Optional.ofNullable(meta.get(key));
    }

    /**
     * Récupère la valeur associée à la clé en tant que String, si possible.
     *
     * @param meta la map des métadonnées
     * @param key la clé à rechercher
     * @return un Optional contenant la chaîne, ou empty si absente, meta null ou non String
     */
    private static Optional<String> getAsString(Map<String, Object> meta, String key) {
        if (meta == null) return Optional.empty();
        Object val = meta.get(key);
        return (val instanceof String) ? Optional.of((String) val) : Optional.empty();
    }

    /**
     * Récupère la valeur associée à la clé en tant que Map, si possible.
     * Utilisé pour des objets JSON/YAML imbriqués.
     *
     * @param meta la map des métadonnées
     * @param key la clé à rechercher
     * @return un Optional contenant la map, ou empty si absente, meta null ou non Map
     */
    @SuppressWarnings("unchecked")
    private static Optional<Map<String, Object>> getAsMap(Map<String, Object> meta, String key) {
        if (meta == null) return Optional.empty();
        Object val = meta.get(key);
        return (val instanceof Map) ? Optional.of((Map<String, Object>) val) : Optional.empty();
    }

    /**
     * Récupère la valeur associée à la clé, parse en arbre YAML et retourne uniquement
     * les structures Map ou List dans les métadonnées du {@link TaskDescriptor}.
     *
     * @param meta la map des métadonnées
     * @param key la clé à rechercher, dont la valeur doit être une chaîne YAML
     * @return un Optional contenant la structure YAML (Map ou List),
     *         ou empty si absente, meta null, non convertible ou type non structuré
     */
    private static Optional<Object> getAsYamlTree(Map<String, Object> meta, String key) {
        Optional<String> yamlString = getAsString(meta, key);
        if (yamlString.isEmpty()) return Optional.empty();
        try {
            Object parsed = YAML_PARSER.load(yamlString.get());
            if (parsed instanceof Map || parsed instanceof List) {
                return Optional.of(parsed);
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            // Optionnel : logger l'erreur ici
            return Optional.empty();
        }
    }

    // Méthodes publiques exposées manipulant uniquement TaskDescriptor ---------------------

    /**
     * Récupère la valeur brute associée à la clé dans les métadonnées du {@link TaskDescriptor}.
     *
     * @param taskDescriptor la tâche contenant les métadonnées
     * @param key la clé à rechercher
     * @return un Optional contenant la valeur brute, ou empty si absente ou task null
     */
    public static Optional<Object> getAsObject(TaskDescriptor taskDescriptor, String key) {
        return taskDescriptor == null ? Optional.empty() : getAsObject(taskDescriptor.getMeta(), key);
    }

    /**
     * Récupère la valeur associée à la clé en tant que String dans les métadonnées du {@link TaskDescriptor}.
     *
     * @param taskDescriptor la tâche contenant les métadonnées
     * @param key la clé à rechercher
     * @return un Optional contenant la chaîne, ou empty si absente, task null ou non String
     */
    public static Optional<String> getAsString(TaskDescriptor taskDescriptor, String key) {
        return taskDescriptor == null ? Optional.empty() : getAsString(taskDescriptor.getMeta(), key);
    }

    /**
     * Récupère la valeur associée à la clé en tant que Map dans les métadonnées du {@link TaskDescriptor}.
     *
     * @param taskDescriptor la tâche contenant les métadonnées
     * @param key la clé à rechercher
     * @return un Optional contenant la map, ou empty si absente, task null ou non Map
     */
    public static Optional<Map<String, Object>> getAsMap(TaskDescriptor taskDescriptor, String key) {
        return taskDescriptor == null ? Optional.empty() : getAsMap(taskDescriptor.getMeta(), key);
    }

    /**
     * Récupère la valeur associée à la clé, parse en arbre YAML et retourne uniquement
     * les structures Map ou List dans les métadonnées du {@link TaskDescriptor}.
     *
     * @param taskDescriptor la tâche contenant les métadonnées
     * @param key la clé à rechercher, dont la valeur doit être une chaîne YAML
     * @return un Optional contenant la structure YAML (Map ou List),
     *         ou empty si absente, task null, non convertible ou type non structuré
     */
    public static Optional<Object> getAsYamlTree(TaskDescriptor taskDescriptor, String key) {
        return taskDescriptor == null ? Optional.empty() : getAsYamlTree(taskDescriptor.getMeta(), key);
    }
}

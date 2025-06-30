package com.r3edge.task.dispatcher;

/**
 * Exception levée lorsqu'une erreur survient durant l'exécution d'une tâche.
 */
public class TaskExecutionException extends RuntimeException {

    /**
     * Construit une nouvelle exception avec le message de détail spécifié.
     *
     * @param message le message de détail.
     */
    public TaskExecutionException(String message) {
        super(message);
    }

    /**
     * Construit une nouvelle exception avec le message de détail et la cause spécifiés.
     *
     * @param message le message de détail.
     * @param cause   la cause de l'erreur.
     */
    public TaskExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}

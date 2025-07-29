package com.r3edge.tasks.dispatcher;

import java.io.Serializable;

/**
 * Interface fonctionnelle représentant une tâche exécutable. Utilisée pour
 * encapsuler une logique différée, notamment avec des planificateurs comme
 * JobRunr.
 */
@FunctionalInterface
public interface TaskLambda extends Serializable {
	/**
	 * Méthode à implémenter contenant la logique de la tâche.
	 *
	 * @throws Exception toute erreur pouvant survenir lors de l'exécution.
	 */
	void run() throws Exception;

	/**
	 * Convertit cette lambda en {@link Runnable}, en encapsulant les exceptions.
	 *
	 * @return un {@link Runnable} prêt à être exécuté.
	 */
	default Runnable toRunnable() {
		return () -> {
			try {
				this.run();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		};
	}
	
    /**
     * Convertit cette lambda en {@link Runnable} avec écoute des événements d'exécution.
     *
     * @param listener écouteur du cycle de vie de la tâche
     * @param task tâche à exécuter
     * @return un {@link Runnable} qui notifie les événements liés à l'exécution
     */
	default Runnable toRunnable(ITaskExecutionListener listener, Task task) {
	    return () -> {
	        listener.onStart(task);
	        try {
	            this.run();
	            listener.onSuccess(task);
	        } catch (Throwable e) {
	            listener.onFailure(task, e);
	            sneakyThrow(e);
	        }
	    };
	}

    /**
     * Lance une exception checked sans avoir à la déclarer.
     *
     * @param e exception à lancer
     * @param <E> type de l'exception
     * @throws E toujours levée, jamais capturée
     */
    @SuppressWarnings("unchecked")
	static <E extends Throwable> void sneakyThrow(Throwable e) throws E {
	    throw (E) e;
	}

}

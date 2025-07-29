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

	@SuppressWarnings("unchecked")
	static <E extends Throwable> void sneakyThrow(Throwable e) throws E {
	    throw (E) e;
	}

}

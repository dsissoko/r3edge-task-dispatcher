package com.r3edge.tasks.dispatcher.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * Service chargé d'invoquer dynamiquement les handlers de tâches.
 */
@Service
@RequiredArgsConstructor
public class TaskInvokerService {
	private static final Logger TECH_LOGGER = LoggerFactory.getLogger(TaskInvokerService.class);
	private final TaskHandlerRegistry registry;
	private final ITaskExecutionListener defaultListener;

	/**
	 * Exécute immédiatement une tâche avec instrumentation et le logger fourni.
	 * @param task La tâche à exécuter.
	 * @param logger Le logger contextuel, ou null pour fallback handler.
	 */
    public void execute(Task task, Logger logger) {
        // Résolution du handler en fonction du type de tâche
        TaskHandler handler = registry.getHandler(task.getType())
            .orElseThrow(() -> new IllegalStateException("Aucun handler pour le type de tâche : " + task.getType()));
        Logger effectiveLogger = resolveLoggerWithWarning(logger, handler);
        // Toujours instrumentation (pipeline listener)
        Runnable r = createRunnable(task, handler, defaultListener, effectiveLogger);
        r.run();
    }

    /**
     * Crée un Runnable instrumenté pour la tâche et le logger fournis.
     * @param task La tâche à exécuter.
     * @param logger Le logger contextuel, ou null pour fallback handler.
     * @return Un Runnable prêt à exécuter la tâche.
     */
    public Runnable createRunnable(Task task, Logger logger) {
        TaskHandler handler = registry.getHandler(task.getType())
            .orElseThrow(() -> new IllegalStateException("Aucun handler pour le type de tâche : " + task.getType()));
        Logger effectiveLogger = resolveLoggerWithWarning(logger, handler);
        return createRunnable(task, handler, defaultListener, effectiveLogger);
    }

    // Version privée conservée pour factorisation interne
    private Runnable createRunnable(Task task, TaskHandler handler, ITaskExecutionListener listener, Logger logger) {
        return () -> {
            try {
                listener.onStart(task, logger);
                handler.handle(task, logger);
                listener.onSuccess(task, logger);
            } catch (Throwable e) {
                listener.onFailure(task, e, logger);
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
	
    /** Résout le logger effectif et loggue un warning si on doit fallback. */
    private Logger resolveLoggerWithWarning(Logger logger, TaskHandler handler) {
        if (logger != null) return logger;
        TECH_LOGGER.warn("Logger was null for handler {}. Using fallback logger for handler class.", handler.getClass().getName());
        return LoggerFactory.getLogger(handler.getClass());
    }
	
}

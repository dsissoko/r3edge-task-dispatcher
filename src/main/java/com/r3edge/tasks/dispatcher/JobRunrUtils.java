package com.r3edge.tasks.dispatcher;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.jobrunr.jobs.context.JobRunrDashboardLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilitaire pour créer un logger compatible avec le tableau de bord JobRunr.
 * À utiliser uniquement dans les classes liées à JobRunr.
 */
public class JobRunrUtils {

    /**
     * Retourne un logger adapté à JobRunr, permettant l'affichage dans son dashboard.
     *
     * @param clazz Classe pour laquelle le logger est instancié
     * @return Logger compatible JobRunr
     */
    public static Logger jobRunrLogger(Class<?> clazz) {
        return new JobRunrDashboardLogger(LoggerFactory.getLogger(clazz));
    }
    
    /**
     * Génére un UUID à partir de l'id fonctionnel de la tâche.
     *
     * @param task  la tâche à déplanifier.
     * @return UUID id unique de la tâche
     */	    
    public static UUID  getJobId(Task task) {
        return getJobId(task.getId());
    }
    
    /**
     * Génére un UUID à partir de l'id fonctionnel de la tâche.
     *
     * @param jobId fonctionnel   la tâche à déplanifier.
     * @return UUID id unique de la tâche
     */	    
    public static UUID  getJobId(String jobId) {
        return UUID.nameUUIDFromBytes(("exec-" + jobId).getBytes(StandardCharsets.UTF_8));
    } 
}

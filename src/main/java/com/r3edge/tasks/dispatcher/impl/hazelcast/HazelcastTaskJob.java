package com.r3edge.tasks.dispatcher.impl.hazelcast;

import java.io.Serializable;

import org.springframework.context.ApplicationContext;

import com.hazelcast.spring.context.SpringAware;
import com.r3edge.tasks.dispatcher.core.TaskDescriptor;
import com.r3edge.tasks.dispatcher.core.TaskInvokerService;

import lombok.RequiredArgsConstructor;

/**
 * Tâche Hazelcast distribuée.
 * 
 * Wrappe un TaskDescriptor et délègue l'exécution au bean Spring TaskInvokerService.
 * Utilisé en interne par HazelcastFireAndForgetExecutor.
 */
@SuppressWarnings("serial")
@RequiredArgsConstructor
@SpringAware
public class HazelcastTaskJob implements Runnable, Serializable {
	/**
	 * Descripteur de la tâche à exécuter.
	 */
    private final TaskDescriptor taskDescriptor;
    private transient ApplicationContext ctx;


    @Override
    public void run() {
        // Récupère ton bean Spring (statiquement ou via ServiceLocator)
    	TaskInvokerService invoker = ctx.getBean(TaskInvokerService.class);
        invoker.execute(taskDescriptor);
    }
}

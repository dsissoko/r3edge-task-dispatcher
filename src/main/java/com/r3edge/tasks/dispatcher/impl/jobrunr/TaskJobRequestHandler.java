package com.r3edge.tasks.dispatcher.impl.jobrunr;

import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import com.r3edge.tasks.dispatcher.TaskInvokerService;

import lombok.RequiredArgsConstructor;

/**
 * Handler JobRunr responsable de l'exécution des requêtes {@link TaskJobRequest}.
 */
@Component
@RequiredArgsConstructor
public class TaskJobRequestHandler implements JobRequestHandler<TaskJobRequest> {

    private final TaskInvokerService invoker;

    @Override
    public void run(TaskJobRequest request) throws Exception {
    	Logger logger = request.getLogger();
        invoker.invokeNow(request.getTask(), logger);
    }
}


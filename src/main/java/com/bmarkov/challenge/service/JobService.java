package com.bmarkov.challenge.service;

import com.bmarkov.challenge.exception.CircularDependenciesException;
import com.bmarkov.challenge.exception.JobException;
import com.bmarkov.challenge.model.Job;
import com.bmarkov.challenge.model.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

/**
 * The service takes care of sorting the tasks
 * to create a proper execution order.
 */
@Service
@Slf4j
public class JobService {
    /**
     * Create a proper execution order.
     * @param job from that job tasks are extracted; not null;
     * @return tasks in proper order
     * @throws CircularDependenciesException in case of circular dependencies.
     * @throws JobException in case of other problem like missing dependency.
     * @throws NullPointerException if job is null.
     *
     */
    public Task[] tasksInOrder(Job job) throws JobException, NullPointerException {
        if (job == null) {
            throw new NullPointerException("job cannot be null");
        }

        // Algorithm:
        //   prepare source pool and target pool
        //   1. take one challenge from source
        //   2. add the dependencies in the target pool
        //        for that challenge if they don't exist
        //        (recursive to point 1.)
        //   3. add the challenge
        //   4. repeat until source pool has tasks

        Task [] tasks = job.getTasks();
        if (tasks == null) {
            // prevent NullPointerException
            tasks = new Task[0];
        }
        Map<String, Task> source = new HashMap<>(
                stream(tasks).collect(toMap(Task::getName, task -> task))
        );
        // Implementation of this map must keep order of insertion
        Map<String, Task> target = new LinkedHashMap<>(source.size());
        Set<String> currentlyProcessed = new HashSet<>();

        log.debug("looping through the tasks");
        while (!source.isEmpty()) {
            Iterator<Task> iterator = source.values().iterator();
            Task task = iterator.next();
            iterator.remove();

            addOneTaskRecursive(source, target, task, currentlyProcessed);
        }
        Task[] array = target.values().toArray(new Task[0]);
        //clear the requires
        for (Task task : array) {
            task.setRequires(null);
        }
        log.debug("Returning {} tasks", target.size());
        return array;
    }

    private void addOneTaskRecursive(Map<String, Task> source, Map<String, Task> target, Task task,
                                     Set<String> currentlyProcessed) throws JobException {
        currentlyProcessed.add(task.getName());
        log.debug("Processing task {}", task.getName());

        String[] requires = task.getRequires();
        if (requires != null && requires.length > 0) {
            for (String taskName : requires) {
                if (!target.containsKey(taskName)) {

                    // detect loop
                    if (currentlyProcessed.contains(taskName)) {
                        throw new CircularDependenciesException("Recursive dependency detected " +
                                "while processing " + task.getName());
                    }

                    // take the challenge from source pool
                    Task dependentTask = source.remove(taskName);
                    if (dependentTask == null) {
                        throw new JobException("missing task name " + taskName + " referenced from " + task.getName());
                    }

                    // add the task and its dependencies recursively
                    addOneTaskRecursive(source, target, dependentTask, currentlyProcessed);
                }

            }
        }
        target.put(task.getName(), Task.cloneWithoutRequires(task));
    }
}

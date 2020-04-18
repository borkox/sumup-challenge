package com.bmarkov.challenge.service;

import com.bmarkov.challenge.model.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * This ervice should be able to return a bash script representation directly.
 */
@Service
@Slf4j
public class BashScriptService {

    public String tasksAsBashScript(Task[] tasks) {
        log.debug("tasksAsBashScript invoked with {} tasks", tasks.length);

        StringBuilder sb = new StringBuilder();
        sb.append("#!/usr/bin/env bash\n");
        sb.append("\n");
        for (Task task : tasks) {
            sb.append(task.getCommand()).append("\n");
        }
        log.debug("tasksAsBashScript returning {} chars", sb.length());

        return sb.toString();
    }
}

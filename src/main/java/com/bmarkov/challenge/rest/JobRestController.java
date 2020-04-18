package com.bmarkov.challenge.rest;

import com.bmarkov.challenge.exception.JobException;
import com.bmarkov.challenge.model.Job;
import com.bmarkov.challenge.model.Task;
import com.bmarkov.challenge.service.BashScriptService;
import com.bmarkov.challenge.service.JobService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

@RestController
@RequestMapping("/api/job")
public class JobRestController {

    private ObjectMapper mapper;
    private JobService jobService;
    private BashScriptService bashScriptService;

    @Autowired
    public JobRestController(ObjectMapper mapper, JobService jobService, BashScriptService bashScriptService) {
        this.mapper = mapper;
        this.jobService = jobService;
        this.bashScriptService = bashScriptService;
    }

    @CrossOrigin("*")
    @PostMapping(value = "/tasksInOrder",
            consumes = {MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_JSON_VALUE, APPLICATION_FORM_URLENCODED_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE)

    public Task[] tasksInOrder(@RequestBody  String jobsJson, @RequestHeader("Content-Type") String contentType) throws JsonProcessingException, JobException, UnsupportedEncodingException {
        jobsJson = handleUrlEncoded(jobsJson, contentType);
        Job job = mapper.readValue(jobsJson, Job.class);
        return jobService.tasksInOrder(job);
    }

    @CrossOrigin("*")
    @PostMapping(value = "/tasksAsBash",
            consumes = {MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_JSON_VALUE, APPLICATION_FORM_URLENCODED_VALUE},
            produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String tasksAsBash(@RequestBody  String jobsJson, @RequestHeader("Content-Type") String contentType)
            throws JsonProcessingException, JobException, UnsupportedEncodingException {
        jobsJson = handleUrlEncoded(jobsJson, contentType);
        Job job = mapper.readValue(jobsJson, Job.class);
        Task[] tasks = jobService.tasksInOrder(job);
        return bashScriptService.tasksAsBashScript(tasks);
    }


    private String handleUrlEncoded(@RequestBody String jobsJson, @RequestHeader("Content-Type") String contentType)
            throws UnsupportedEncodingException {
        if (contentType.startsWith(APPLICATION_FORM_URLENCODED_VALUE)) {
            jobsJson = URLDecoder.decode(jobsJson, "UTF-8");
        }
        return jobsJson;
    }
}

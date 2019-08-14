package com.mazhangjing.fast.task_server.web;

import com.mazhangjing.fast.task_server.entity.Status;
import com.mazhangjing.fast.task_server.entity.Task;
import com.mazhangjing.fast.task_server.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import scala.Option;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("job/")
public class JobController {

    private final TaskService taskService;

    @Autowired
    public JobController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("{groupId}") public String groupInfo(
            @PathVariable String groupId, Map<String,Object> map) {
        map.put("groupId", groupId);
        return "group.html";
    }

    @ResponseBody
    @GetMapping("{groupId}/fetch")
    public Object fetchJobs(
            @PathVariable String groupId,
            @RequestParam String runner,
            @RequestParam int number,
            @RequestParam int workerPromiseReturnSeconds) {
        return taskService.fetchJob(groupId, runner, number, workerPromiseReturnSeconds);
    }

    @ResponseBody
    @PostMapping("{groupId}/finish")
    public Object finishJobs(
            @PathVariable String groupId,
            @RequestParam String runner,
            @RequestParam Long taskId,
            @RequestParam String status,
            @RequestParam String result,
            @RequestParam(required = false, defaultValue = "") String note) {
        Status statusEnum = Enum.valueOf(Status.class, status);
        Option<Task> taskOption = taskService.finishJob(groupId, runner, taskId, statusEnum, result, note);
        Map<String, Object> res = new HashMap<>();
        res.put("taskId", taskId);
        res.put("groupId", groupId);
        res.put("workStatus", status);
        res.put("note", note);
        res.put("finishStatus", taskOption.isDefined() ? "1" : "0");
        if (taskOption.isDefined()) {
            res.put("bean", taskOption.get());
        } else {
            res.put("bean","");
        }
        return res;
    }
}

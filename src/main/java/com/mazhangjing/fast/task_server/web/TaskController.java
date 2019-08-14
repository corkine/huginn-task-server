package com.mazhangjing.fast.task_server.web;

import com.mazhangjing.fast.task_server.entity.Status;
import com.mazhangjing.fast.task_server.service.TaskConvert;
import com.mazhangjing.fast.task_server.service.TaskService;
import com.mazhangjing.fast.task_server.service.TaskSkeleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("task/")
public class TaskController {

    private final TaskService taskService;

    private final TaskConvert taskConvert;

    private static List<String> defined_type = Arrays.asList("NEW", "SUBMITTED", "FINISHED", "FAILED", "SUSPEND");

    @Autowired
    public TaskController(TaskService taskService, TaskConvert taskConvert) {
        this.taskService = taskService;
        this.taskConvert = taskConvert;
    }

    //类型：FINISHED, FAILED, SUBMITTED, ALL
    @ResponseBody
    @GetMapping("{groupId}/fetch") public Object fetchTask(
            @PathVariable String groupId,
            @RequestParam(required = false, defaultValue = "ALL") String type) {

        if (type.toUpperCase().equals("ALL")) {
            return taskService.taskGet().getTasksByTaskGroup(groupId);
        } else if (defined_type.contains(type.toUpperCase())) {
            return taskService.taskGet()
                    .getTasksByTaskGroupAndStatus(groupId, Enum.valueOf(Status.class, type.toUpperCase()));
        } else return "";
    }

    @ResponseBody
    @PostMapping("push") public Object pushTasks(
            @RequestParam Integer maxWorkerRetryTime,
            @RequestBody List<TaskSkeleton> contents) {
        return taskService.batchUpload(maxWorkerRetryTime,
                taskConvert.convertSkeletonToTask(contents));
    }



}

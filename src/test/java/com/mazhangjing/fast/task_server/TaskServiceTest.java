package com.mazhangjing.fast.task_server;


import com.mazhangjing.fast.task_server.dao.TaskAccess;
import com.mazhangjing.fast.task_server.dao.TaskGet;
import com.mazhangjing.fast.task_server.entity.Status;
import com.mazhangjing.fast.task_server.entity.Task;
import com.mazhangjing.fast.task_server.service.TaskConvert;
import com.mazhangjing.fast.task_server.service.TaskService;
import com.mazhangjing.fast.task_server.service.TaskSkeleton;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import scala.Option;

import java.util.Arrays;
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringRunner.class)
@SpringBootTest
public class TaskServiceTest {

    @Autowired
    TaskGet taskGet;

    @Autowired
    TaskAccess taskAccess;

    @Autowired
    TaskService taskService;

    @Autowired
    TaskConvert taskConvert;

    private static Task firstGetTask;

    @Test public void A1_clearAll() {
        taskAccess.deleteAll();
    }

    @Test
    public void A2_writeData() {
        Task task = new Task("g1", "{'id':27, 'ser_number':100'}");
        taskAccess.save(task);
        firstGetTask = task;
        System.out.println("Saving firstGetTask = " + task);

        Task task2 = new Task("g1", "{'id':28, 'ser_number':200'}");
        taskAccess.save(task2);
        System.out.println("Saving SecondGetTask = " + task2);
    }

    @Test
    public void A3_testReadThisData() {
        System.out.println("firstGetTask = " + firstGetTask.getId());
        Option<Task> taskById = taskGet.getTaskById(firstGetTask.getId());
        System.out.println("taskById = " + taskById);
        assert (taskById.isDefined());
        Option<Task> noTask = taskGet.getTaskById(Long.MAX_VALUE);
        assert (noTask.isEmpty());
    }

    @Test
    public void A4_testTaskServiceFetchData() {
        System.out.println("All task: " + taskGet.findAll());
        List<Task> tasks = taskService.fetchJob("g1", "Runner", 10, 200);
        System.out.println("tasks = " + tasks);
        assert (tasks != null && tasks.size() == 2);
        System.out.println("tasks = " + tasks.get(0));
        Task task = tasks.get(0);
        assert (task.getStatus().equals(Status.SUBMITTED));
        assert (!task.getInformation().isEmpty());
    }

    @Test
    public void B_testTaskServiceInRealWay() {
        firstGetTask.setStatus(Status.SUBMITTED);
        taskAccess.save(firstGetTask);
        Option<Task> taskOption = taskService.finishJob("g1", "Runner1", firstGetTask.getId(), Status.FINISHED, "Result Here", "Note1");
        assert (taskOption.isDefined());
    }

    @Test
    public void B_testTaskServiceWithBadGroup() {
        firstGetTask.setStatus(Status.SUBMITTED);
        taskAccess.save(firstGetTask);
        Option<Task> taskOption = taskService.finishJob("g2", "Runner1",firstGetTask.getId(), Status.FINISHED, "Result Here", "Note1");
        assert (taskOption.isEmpty());
    }

    @Test
    public void B_testTaskServiceWithBadId() {
        firstGetTask.setStatus(Status.SUBMITTED);
        taskAccess.save(firstGetTask);
        Option<Task> taskOption = taskService.finishJob("g1", "Runner1",firstGetTask.getId() + 233, Status.FINISHED, "Result Here", "Note1");
        assert (taskOption.isEmpty());
    }

    @Test
    public void B_testTaskServiceWithFailure() {
        firstGetTask.setStatus(Status.SUBMITTED);
        taskAccess.save(firstGetTask);
        Option<Task> taskOption = taskService.finishJob("g1","Runner1", firstGetTask.getId(), Status.FAILED, "Result Here", "");
        assert (taskOption.isDefined());
    }

    @Test
    public void C_testBatchUploadAndSkeletonConvert() {
        List<TaskSkeleton> taskSkeletons = Arrays.asList(new TaskSkeleton("g1", "{d1}"), new TaskSkeleton("g1", "{d1}"), new TaskSkeleton("g1", "{d1}")
                , new TaskSkeleton("g1", "{d1}"), new TaskSkeleton("g1", "{d1}"), new TaskSkeleton("g1", "{d1}"),
                new TaskSkeleton("g2", "{d1}"), new TaskSkeleton("g2", "{d1}"), new TaskSkeleton("g2", "{d1}"));
        List<Task> tasks = taskConvert.convertSkeletonToTask(taskSkeletons);
        assert (tasks.size() == 9);
        List<Task> tasks1 = taskService.batchUpload(5, tasks);
        assert (tasks1.size() == 9);
        for (Task t : tasks1) {
            assert (t.getStatus().equals(Status.NEW));
        }
        List<Task> all = taskGet.findAll();
        assert (all.size() == 11);
    }

    @Test
    public void Z_clearAll() {
        taskAccess.deleteAll();
    }


}


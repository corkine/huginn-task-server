package com.mazhangjing.fast.task_server;

import com.mazhangjing.fast.task_server.dao.TaskAccess;
import com.mazhangjing.fast.task_server.dao.TaskGet;
import com.mazhangjing.fast.task_server.entity.Status;
import com.mazhangjing.fast.task_server.entity.Task;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import scala.Option;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringRunner.class)
@SpringBootTest
public class TaskDaoTest {

    @Autowired TaskGet taskGet;

    @Autowired
    TaskAccess taskAccess;

    private static Task writeTask;

    private static Task firstTask;

    @Test public void A1_clearAll() {
        taskAccess.deleteAll();
    }

    @Test
    public void A2_testTaskAccess() {
        Task task = new Task("g1", "{'id':27, 'ser_number':100'}");
        taskAccess.save(task);
        writeTask = task;
        System.out.println("Saving writeTask = " + task);
    }

    @Test
    public void A3_testByIdGet() {
        System.out.println("writeTask = " + writeTask.getId());
        Option<Task> taskById = taskGet.getTaskById(writeTask.getId());
        System.out.println("taskById = " + taskById);
        assert (taskById.isDefined());
        Option<Task> noTask = taskGet.getTaskById(Long.MAX_VALUE);
        assert (noTask.isEmpty());
    }

    @Test
    public void B_testTaskGetAndWrite() {
        List<Task> tasks = taskGet.getTasksByUpdateTimeAfter(Date.from(Instant.now().minusSeconds(100000)));
        assert (tasks.size() > 0);
        Task task = tasks.get(0);
        System.out.println("task = " + task);
        Date updateTime = task.getUpdateTime();
        task.setResult("Result");
        assert (task.getUpdateTime().after(updateTime));
        assert (task.getStatus().equals(Status.FINISHED));
        firstTask = task;
        taskAccess.save(firstTask);
    }

    @Test
    public void C_testAfterTaskWriteAccess() {
        System.out.println("firstTask = " + firstTask);
        System.out.println("writeTask = " + writeTask);
        assert (firstTask.getStatus().equals(Status.FINISHED));
        assert (firstTask.getUpdateTime().after(writeTask.getUpdateTime()));
    }

}

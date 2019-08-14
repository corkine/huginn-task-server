package com.mazhangjing.fast.task_server.dao;

import com.mazhangjing.fast.task_server.entity.Status;
import com.mazhangjing.fast.task_server.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import scala.Option;

import java.util.Date;
import java.util.List;

@SuppressWarnings("SpringDataRepositoryMethodReturnTypeInspection")
public interface TaskGet extends PagingAndSortingRepository<Task, Long> {

    List<Task> getTasksByStatus(Status status);

    List<Task> getTasksByUpdateTimeBetween(Date from, Date end);

    List<Task> getTasksByUpdateTimeAfter(Date from);

    List<Task> getTasksByTaskGroup(String taskGroup);

    List<Task> getTasksByTaskGroupAndStatus(String taskGroup, Status status);

    @Query(value = "select t from Task t where t.taskGroup = ?1 and t.status = ?2")
    Page<Task> queryForNeeded(String taskGroup, Status status, Pageable pageable);

    Option<Task> getTaskById(Long id);
    List<Task> findAll();
}

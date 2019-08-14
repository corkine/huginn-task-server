package com.mazhangjing.fast.task_server.dao;

import com.mazhangjing.fast.task_server.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskAccess extends JpaRepository<Task, Long> {
}

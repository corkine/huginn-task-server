package com.mazhangjing.fast.task_server.corns

import java.util.Date

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.client.fluent.Request
import org.apache.http.util.EntityUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.{EnableScheduling, Scheduled}
import org.springframework.stereotype.Component
import java.util.{ArrayList => JList, HashMap => JMap}

import com.mazhangjing.fast.task_server.service.TaskService

import scala.beans.BeanProperty

@Configuration
@EnableScheduling
class SchedulerConfig {
}

@Component
@ConfigurationProperties(prefix = "schedule")
class SchedulerBean {

  private val logger = LoggerFactory.getLogger(classOf[SchedulerBean])

  @Autowired var taskService: TaskService = _

  @BeanProperty var fixDurationMillSeconds: Long = 0

  @Scheduled(fixedRate = 120000L) def doWork(): Unit = {
    taskService.checkingSubmitTasks()
    taskService.checkingFailedTasks()
  }

}

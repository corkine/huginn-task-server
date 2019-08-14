package com.mazhangjing.fast.task_server.service

import java.time.{Duration, LocalDateTime, ZoneId}
import java.util

import com.mazhangjing.fast.task_server.dao.{TaskAccess, TaskGet}
import com.mazhangjing.fast.task_server.entity.{Status, Task}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.{List => JList}

import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._

@Service(value = "taskService")
class TaskService {

  val logger: Logger = LoggerFactory.getLogger(classOf[TaskService])

  @Autowired var taskAccess: TaskAccess = _
  @Autowired var taskGet: TaskGet = _

  def Try[T](op: => Option[T]): Option[T] = {
    try {
      op
    } catch {
      case e :Throwable => e.printStackTrace(System.err); None
    }
  }

  //对一个 SUBMITTED 的匹配 taskId 和 groupId 的条目更新信息，可能为 FINISHED 或者 FAILED 或者 NEW（交给别人，不记录错误）？
  def finishJob(groupId:String, runner:String, taskId:Long, status: Status, result:String, note:String): Option[Task] = Try {
    taskGet.getTaskById(taskId) match {
      case Some(task) =>
        if (task.getTaskGroup != groupId)
          throw new RuntimeException("数据提交到了错误的分组 - 组和数据无法匹配")
        task.setTaskGroup(groupId)
         if (!task.getStatus.equals(Status.SUBMITTED))
           throw new RuntimeException(s"提交的数据并不能匹配到一个待提交的任务，根据 Id 找到的任务状态为 ${task.getStatus},期望为 SUBMITTED")
        task.setStatus(status)
        task.setResult(result)
        task.setInformation(task.getInformation + s"\nSubmit By $runner at ${LocalDateTime.now()} - [NOTE: $note]")
        taskAccess.save(task)
        Option(task)
      case None => None
    }
  }

  def fetchJob(groupId:String, runner:String, number:Int, workerPromiseReturnSeconds:Int): JList[Task] =  try {
    //对于 Failed、超时 的情况而言，根据 maxWorkerRetryTime 以及使用自带任务定时将其变为 NEW
    val tasks = taskGet.queryForNeeded(groupId, Status.NEW, PageRequest.of(0, number))
    val content = tasks.getContent.asScala
    val settedTasks = content.map(task => {
      task.setStatus(Status.SUBMITTED)
      task.setWorkerPromiseReturnSeconds(workerPromiseReturnSeconds)
      task.setInformation(s"Fetch by $runner at ${LocalDateTime.now()}")
      task
    })
    val savedTasks = taskAccess.saveAll(settedTasks.asJava)
    savedTasks
  } catch {
    case e: Throwable => e.printStackTrace(System.err); new util.ArrayList[Task]()
  }

  def batchUpload(maxWorkerRetryTime:Int, tasks: JList[Task]): JList[Task] = try {
    taskAccess.saveAll(tasks.asScala.map(t => {
      t.setRemainFailedRetryTime(maxWorkerRetryTime); t
    }).asJava)
  } catch {
    case e: Throwable => e.printStackTrace(System.err); new util.ArrayList[Task]()
  }

  def checkingFailedTasks(): Unit = {
    val failedTask = taskGet.getTasksByStatus(Status.FAILED)
    val allFailedTasks = failedTask.asScala.map(task => {
      if (task.getRemainFailedRetryTime > 0) {
        task.setRemainFailedRetryTime(task.getRemainFailedRetryTime - 1)
        task.setInformation(task.getInformation + s"\nScheduler Retry This Task at ${LocalDateTime.now()}")
        task.setStatus(Status.NEW)
        task
      } else {
        task.setInformation(task.getInformation + s"\nScheduler Suspend This Task at ${LocalDateTime.now()}")
        task.setStatus(Status.SUSPEND)
        task
      }
    }).asJava
    if (allFailedTasks.size() > 0) {
      logger.info("Checked Failed Task, Change They State to Retry or Suspend now...")
      taskAccess.saveAll(allFailedTasks)
    }
  }

  def checkingSubmitTasks(): Unit = {
    val submitTasks = taskGet.getTasksByStatus(Status.SUBMITTED)
    val now = LocalDateTime.now()
    val failedTasks = submitTasks.asScala.collect {
      case task if {
        val lastUpdate = task.getUpdateTime
        val promiseTrySeconds = task.getWorkerPromiseReturnSeconds
        //如果最后一次更新到现在的时间超过了规定时间，那么标记任务失败
        val err = Duration.between(LocalDateTime.ofInstant(lastUpdate.toInstant, ZoneId.systemDefault()), now)
          .abs().getSeconds > promiseTrySeconds
        err
      } =>
        task.setStatus(Status.FAILED)
        task
    }.asJava
    if (failedTasks.size() > 0) {
      logger.info(s"Find Failed Task in SUBMIT Status: $failedTasks, Setting Failure Now...")
      taskAccess.saveAll(failedTasks)
    }
  }
}

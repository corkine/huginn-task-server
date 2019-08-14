package com.mazhangjing.fast.task_server.service
import java.util.{List => JList}
import com.mazhangjing.fast.task_server.entity.Task
import org.springframework.stereotype.Component
import scala.collection.JavaConverters._


@Component(value = "taskConvert")
class TaskConvert {

    def convertSkeletonToTask(list: JList[TaskSkeleton]): JList[Task] = {
        list.asScala.map(sk => new Task(sk.group, sk.data)).asJava
    }

}

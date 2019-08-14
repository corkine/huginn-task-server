package com.mazhangjing.fast.task_server.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "TASK_TAB")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    Integer remainFailedRetryTime = 5;

    Integer workerPromiseReturnSeconds = 1200;

    @Column(nullable = false)
    String taskGroup;

    @Column(nullable = false)
    String data;

    @Access(AccessType.PROPERTY)
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    Status status = Status.NEW;

    @Access(AccessType.PROPERTY)
    String result;

    @Column(length = 4000)
    String information;

    //设置 status 获取，以及返回结果时，都会自动更新时间
    //为了支持自动触发 status、result 方法而非字段，使用 Access 注解
    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(nullable = false)
    Date updateTime;

    public Task(String taskGroup, String data, Status status, Date updateTime) {
        this.taskGroup = taskGroup;
        this.data = data;
        this.status = status;
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", remainFailedRetryTime=" + remainFailedRetryTime +
                ", workerPromiseReturnSeconds=" + workerPromiseReturnSeconds +
                ", taskGroup='" + taskGroup + '\'' +
                ", data='" + data + '\'' +
                ", status=" + status +
                ", result='" + result + '\'' +
                ", information='" + information + '\'' +
                ", updateTime=" + updateTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id) &&
                Objects.equals(remainFailedRetryTime, task.remainFailedRetryTime) &&
                Objects.equals(workerPromiseReturnSeconds, task.workerPromiseReturnSeconds) &&
                Objects.equals(taskGroup, task.taskGroup) &&
                Objects.equals(data, task.data) &&
                status == task.status &&
                Objects.equals(result, task.result) &&
                Objects.equals(information, task.information) &&
                Objects.equals(updateTime, task.updateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, remainFailedRetryTime, workerPromiseReturnSeconds, taskGroup, data, status, result, information, updateTime);
    }

    public Integer getRemainFailedRetryTime() {
        return remainFailedRetryTime;
    }

    public void setRemainFailedRetryTime(Integer remainFailedRetryTime) {
        this.remainFailedRetryTime = remainFailedRetryTime;
    }

    public Integer getWorkerPromiseReturnSeconds() {
        return workerPromiseReturnSeconds;
    }

    public void setWorkerPromiseReturnSeconds(Integer workerPromiseReturnSeconds) {
        this.workerPromiseReturnSeconds = workerPromiseReturnSeconds;
    }

    public Task(String taskGroup, String data, Status status) {
        this.taskGroup = taskGroup;
        this.data = data;
        this.status = status;
        this.updateTime = new Date();
    }

    public Task(String taskGroup, String data) {
        this.taskGroup = taskGroup;
        this.data = data;
        this.updateTime = new Date();
    }

    public Long getId() {
        return id;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskGroup() {
        return taskGroup;
    }

    public void setTaskGroup(String taskGroup) {
        this.taskGroup = taskGroup;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
        this.updateTime = new Date();
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
        this.setStatus(Status.FINISHED);
        this.updateTime = new Date();
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Task() {
    }
}
